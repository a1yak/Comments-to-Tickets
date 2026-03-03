package comment.to.ticket.controller;

import comment.to.ticket.model.Ticket;
import comment.to.ticket.service.TicketsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/tickets")
public class TicketsController {

    private final TicketsService ticketsService;

    @GetMapping()
    public ResponseEntity<List<Ticket>> getTickets(){

        List<Ticket> tickets = ticketsService.getTickets();
        return  ResponseEntity.ok(tickets);
    }
}

