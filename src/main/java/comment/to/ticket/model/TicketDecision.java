package comment.to.ticket.model;

import lombok.Data;

@Data
public class TicketDecision {

    private boolean createTicket;
    private String title;
    private String category;   // bug | feature | billing | account | other
    private String priority;   // low | medium | high
    private String summary;

}
