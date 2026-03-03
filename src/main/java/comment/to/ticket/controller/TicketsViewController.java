package comment.to.ticket.controller;

import comment.to.ticket.service.TicketsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class TicketsViewController {

    private final TicketsService ticketsService;

    @GetMapping("/tickets")
    public String viewTickets(Model model) {
        model.addAttribute("tickets", ticketsService.getTickets());
        return "tickets";
    }
}
