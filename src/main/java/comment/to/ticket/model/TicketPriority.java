package comment.to.ticket.model;

public enum TicketPriority {

    LOW,
    MEDIUM,
    HIGH;

    public static TicketPriority from(String value) {
        return TicketPriority.valueOf(value.trim().toUpperCase());
    }
}
