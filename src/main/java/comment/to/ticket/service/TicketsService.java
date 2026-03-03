package comment.to.ticket.service;

import comment.to.ticket.model.Ticket;
import comment.to.ticket.model.TicketCategory;
import comment.to.ticket.model.TicketDecision;
import comment.to.ticket.model.TicketPriority;
import comment.to.ticket.repository.TicketsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketsService {

    private final TicketsRepository ticketsRepository;

    public void createTicket(TicketDecision ticketDecision){
        Ticket ticket = new Ticket();
        ticket.setTitle(ticketDecision.getTitle());
        ticket.setCategory(TicketCategory.from(ticketDecision.getCategory()));
        ticket.setPriority(TicketPriority.from(ticketDecision.getPriority()));
        ticket.setSummary(ticketDecision.getSummary());
        ticketsRepository.save(ticket);
    }

    public List<Ticket> getTickets(){
        return ticketsRepository.findAll();
    }
}
