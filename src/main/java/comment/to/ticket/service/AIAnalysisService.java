package comment.to.ticket.service;

import comment.to.ticket.model.TicketDecision;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class AIAnalysisService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String model;

    public AIAnalysisService(
            WebClient.Builder webClientBuilder,
            ObjectMapper objectMapper,
            @Value("${huggingface.base-url}") String hfBaseUrl,
            @Value("${huggingface.api.token}") String token,
            @Value("${huggingface.model}") String model
    ) {
        this.webClient = webClientBuilder
                .baseUrl(hfBaseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        this.objectMapper = objectMapper;
        this.model = model;
    }

    public TicketDecision analyze(String commentText) {

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content",
                                "You are an assistant that outputs ONLY valid JSON. No explanation, no markdown, no extra text."),
                        Map.of("role", "user", "content", buildPrompt(commentText))
                ),
                "max_tokens", 300,
                "temperature", 0.1
        );

        String rawResponse;
        try {
            rawResponse = webClient.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class).map(errorBody -> {
                                System.err.println("HF API ERROR: " + errorBody);
                                return new RuntimeException("HF API error: " + errorBody);
                            })
                    )
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(30));
        } catch (Exception e) {
            System.err.println("Request failed: " + e.getMessage());
            return fallbackDecision();
        }

        System.out.println("RAW RESPONSE: " + rawResponse);

        if (rawResponse == null) {
            return fallbackDecision();
        }

        String generatedText = extractChatCompletionText(rawResponse);
        System.out.println("EXTRACTED TEXT: " + generatedText);

        String jsonCandidate = findJsonInText(generatedText);
        if (jsonCandidate != null) {
            try {
                return objectMapper.readValue(jsonCandidate, TicketDecision.class);
            } catch (Exception e) {
                System.err.println("JSON parse failed: " + e.getMessage());
            }
        }

        return heuristicParse(generatedText);
    }

    private String buildPrompt(String commentText) {
        return """
                Analyze this user comment and decide if it needs a support ticket.
                Respond ONLY with valid JSON in this exact format, nothing else:
                {
                  "createTicket": true or false,
                  "title": "short title here if ticket needs to be created",
                  "category": "bug",
                  "priority": "high",
                  "summary": "one sentence summary here"
                }
                Category must be one of: bug, feature, billing, account, other
                Priority must be one of: low, medium, high
                
                Comment: "%s"
                """.formatted(commentText);
    }

    // Parses OpenAI-compatible chat completions response:
    // { "choices": [ { "message": { "content": "..." } } ] }
    private String extractChatCompletionText(String rawResponse) {
        try {
            JsonNode root = objectMapper.readTree(rawResponse);
            return root
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();
        } catch (Exception e) {
            System.err.println("Could not extract content from response: " + e.getMessage());
            return rawResponse;
        }
    }

    private String findJsonInText(String text) {
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1);
        }
        return null;
    }

    private TicketDecision heuristicParse(String text) {
        TicketDecision decision = fallbackDecision();
        String lower = text.toLowerCase();

        if (lower.contains("bug") || lower.contains("error") || lower.contains("fail") || lower.contains("not working")) {
            decision.setCreateTicket(true);
            decision.setCategory("bug");
        } else if (lower.contains("feature") || lower.contains("would like") || lower.contains("please add")) {
            decision.setCreateTicket(true);
            decision.setCategory("feature");
        } else if (lower.contains("bill") || lower.contains("charge") || lower.contains("payment")) {
            decision.setCreateTicket(true);
            decision.setCategory("billing");
        } else if (lower.contains("login") || lower.contains("account") || lower.contains("password")) {
            decision.setCreateTicket(true);
            decision.setCategory("account");
        }

        if (decision.isCreateTicket()) {
            decision.setTitle(truncate(text, 60));
            decision.setSummary(truncate(text, 200));

            if (lower.contains("urgent") || lower.contains("can't") || lower.contains("cannot")) {
                decision.setPriority("high");
            } else if (lower.contains("important") || lower.contains("please")) {
                decision.setPriority("medium");
            } else {
                decision.setPriority("low");
            }
        }

        return decision;
    }

    private TicketDecision fallbackDecision() {
        TicketDecision ticketDecision = new TicketDecision();
        ticketDecision.setCreateTicket(false);
        ticketDecision.setTitle(null);
        ticketDecision.setCategory("other");
        ticketDecision.setPriority("low");
        ticketDecision.setSummary(null);
        return ticketDecision;
    }

    private String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max - 1);
    }
}