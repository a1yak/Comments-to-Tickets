package comment.to.ticket.service;

import comment.to.ticket.model.Ticket;
import comment.to.ticket.model.TicketDecision;
import comment.to.ticket.model.TicketCategory;
import comment.to.ticket.model.TicketPriority;
import comment.to.ticket.repository.TicketsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketsServiceTest {

    @Mock
    private TicketsRepository ticketsRepository;

    @InjectMocks
    private TicketsService ticketsService;

    @Test
    void createTicket_shouldMapDecisionAndSave() {
        TicketDecision decision = new TicketDecision();
        decision.setTitle("Bug in app");
        decision.setCategory("bug");
        decision.setPriority("high");
        decision.setSummary("App crashes when clicking save");

        ticketsService.createTicket(decision);

        verify(ticketsRepository, times(1)).save(argThat(ticket ->
                ticket.getTitle().equals("Bug in app") &&
                        ticket.getCategory() == TicketCategory.BUG &&
                        ticket.getPriority() == TicketPriority.HIGH &&
                        ticket.getSummary().equals("App crashes when clicking save")
        ));
    }

    @Test
    void getTickets_shouldReturnAllTickets() {
        Ticket t1 = new Ticket();
        t1.setTitle("Ticket 1");

        Ticket t2 = new Ticket();
        t2.setTitle("Ticket 2");

        when(ticketsRepository.findAll()).thenReturn(List.of(t1, t2));

        List<Ticket> tickets = ticketsService.getTickets();

        assertEquals(2, tickets.size());
        assertEquals("Ticket 1", tickets.get(0).getTitle());
        assertEquals("Ticket 2", tickets.get(1).getTitle());

        verify(ticketsRepository, times(1)).findAll();
    }
}