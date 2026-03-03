package comment.to.ticket.controller;

import static comment.to.ticket.model.TicketPriority.*;
import static org.junit.jupiter.api.Assertions.*;

import comment.to.ticket.model.Ticket;
import comment.to.ticket.model.TicketCategory;
import comment.to.ticket.model.TicketPriority;
import comment.to.ticket.service.TicketsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TicketsViewController.class)
class TicketsViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TicketsService ticketService;

    @Test
    void getTickets_returnsTicketsView() throws Exception {
        when(ticketService.getTickets()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/tickets"))
                .andExpect(status().isOk())
                .andExpect(view().name("tickets"));
    }

    @Test
    void getTickets_addsTicketListToModel() throws Exception {
        when(ticketService.getTickets()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/tickets"))
                .andExpect(model().attributeExists("tickets"));
    }

    @Test
    void getTickets_modelContainsEmptyList_whenNoTicketsExist() throws Exception {
        when(ticketService.getTickets()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/tickets"))
                .andExpect(model().attribute("tickets", hasSize(0)));
    }

    @Test
    void getTickets_modelContainsAllTickets_whenTicketsExist() throws Exception {
        List<Ticket> tickets = List.of(
                buildTicket(1, "Login broken",       TicketCategory.BUG,     HIGH,   "User cannot log in"),
                buildTicket(2, "Add dark mode",      TicketCategory.FEATURE, LOW,    "User requests dark mode"),
                buildTicket(3, "Wrong charge on bill",TicketCategory.BILLING, MEDIUM, "User was charged twice")
        );
        when(ticketService.getTickets()).thenReturn(tickets);

        mockMvc.perform(get("/tickets"))
                .andExpect(model().attribute("tickets", hasSize(3)))
                .andExpect(model().attribute("tickets", contains(tickets.toArray())));
    }

    @Test
    void getTickets_callsServiceOnce() throws Exception {
        when(ticketService.getTickets()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/tickets"));

        verify(ticketService, times(1)).getTickets();
    }

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