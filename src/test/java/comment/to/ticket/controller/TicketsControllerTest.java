package comment.to.ticket.controller;

import comment.to.ticket.model.Ticket;
import comment.to.ticket.model.TicketCategory;
import comment.to.ticket.model.TicketPriority;
import comment.to.ticket.service.TicketsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TicketsController.class)
class TicketsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TicketsService ticketsService;

    // ─── GET /v1/tickets ─────────────────────────────────────────────────────

    @Test
    void getTickets_returns200WithEmptyList_whenNoTicketsExist() throws Exception {
        when(ticketsService.getTickets()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/v1/tickets"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getTickets_returns200WithAllTickets_whenTicketsExist() throws Exception {
        List<Ticket> tickets = List.of(
                buildTicket(1, "Login broken",         TicketCategory.BUG,     TicketPriority.HIGH,   "User cannot log in"),
                buildTicket(2, "Add dark mode",        TicketCategory.FEATURE, TicketPriority.LOW,    "User requests dark mode"),
                buildTicket(3, "Wrong billing charge", TicketCategory.BILLING, TicketPriority.MEDIUM, "User was charged twice")
        );
        when(ticketsService.getTickets()).thenReturn(tickets);

        mockMvc.perform(get("/v1/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id",       is(1)))
                .andExpect(jsonPath("$[0].title",    is("Login broken")))
                .andExpect(jsonPath("$[0].category", is("BUG")))
                .andExpect(jsonPath("$[0].priority", is("HIGH")))
                .andExpect(jsonPath("$[0].summary",  is("User cannot log in")))
                .andExpect(jsonPath("$[1].id",       is(2)))
                .andExpect(jsonPath("$[2].id",       is(3)));
    }

    @Test
    void getTickets_callsServiceOnce() throws Exception {
        when(ticketsService.getTickets()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/v1/tickets"));

        verify(ticketsService, times(1)).getTickets();
    }

    @Test
    void getTickets_returnsCorrectContentType() throws Exception {
        when(ticketsService.getTickets()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/v1/tickets"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void getTickets_ticketFieldsAreCorrectlySerialised() throws Exception {
        Ticket ticket = buildTicket(42, "Password reset fails", TicketCategory.BUG, TicketPriority.HIGH, "User cannot reset password");
        when(ticketsService.getTickets()).thenReturn(List.of(ticket));

        mockMvc.perform(get("/v1/tickets"))
                .andExpect(jsonPath("$[0].id",       is(42)))
                .andExpect(jsonPath("$[0].title",    is("Password reset fails")))
                .andExpect(jsonPath("$[0].category", is("BUG")))
                .andExpect(jsonPath("$[0].priority", is("HIGH")))
                .andExpect(jsonPath("$[0].summary",  is("User cannot reset password")));
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private Ticket buildTicket(Integer id, String title, TicketCategory category, TicketPriority priority, String summary) {
        Ticket ticket = new Ticket();
        ticket.setId(id);
        ticket.setTitle(title);
        ticket.setCategory(category);
        ticket.setPriority(priority);
        ticket.setSummary(summary);
        return ticket;
    }
}