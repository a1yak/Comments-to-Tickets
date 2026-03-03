package comment.to.ticket.controller;

import comment.to.ticket.model.Comment;
import comment.to.ticket.service.CommentsService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentsController.class)
class CommentsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentsService commentsService;

    // ─── GET /v1/comments ────────────────────────────────────────────────────

    @Test
    void getComments_returns200WithEmptyList_whenNoCommentsExist() throws Exception {
        when(commentsService.getAllComments()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/v1/comments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getComments_returns200WithAllComments_whenCommentsExist() throws Exception {
        List<Comment> comments = List.of(
                buildComment(1, "App crashes on login"),
                buildComment(2, "Please add dark mode"),
                buildComment(3, "Great product, love it!")
        );
        when(commentsService.getAllComments()).thenReturn(comments);

        mockMvc.perform(get("/v1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].commentText", is("App crashes on login")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[2].id", is(3)));
    }

    @Test
    void getComments_callsServiceOnce() throws Exception {
        when(commentsService.getAllComments()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/v1/comments"));

        verify(commentsService, times(1)).getAllComments();
    }

    // ─── POST /v1/comments ───────────────────────────────────────────────────

    @Test
    void addComment_returns201WithSavedComment() throws Exception {
        Comment incoming = buildComment(null, "I cannot reset my password");
        Comment saved    = buildComment(10,  "I cannot reset my password");

        when(commentsService.addComment(any(Comment.class))).thenReturn(saved);

        mockMvc.perform(post("/v1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incoming)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.commentText", is("I cannot reset my password")));
    }

    @Test
    void addComment_callsServiceWithReceivedComment() throws Exception {
        Comment incoming = buildComment(null, "Billing charged me twice");
        when(commentsService.addComment(any(Comment.class))).thenReturn(incoming);

        mockMvc.perform(post("/v1/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(incoming)));

        verify(commentsService, times(1)).addComment(any(Comment.class));
    }

    @Test
    void addComment_returns400_whenBodyIsMissing() throws Exception {
        mockMvc.perform(post("/v1/comments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(commentsService);
    }

    @Test
    void addComment_returns415_whenContentTypeIsNotJson() throws Exception {
        mockMvc.perform(post("/v1/comments")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("plain text comment"))
                .andExpect(status().isUnsupportedMediaType());

        verifyNoInteractions(commentsService);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private Comment buildComment(Integer id, String text) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setCommentText(text);
        return comment;
    }
}