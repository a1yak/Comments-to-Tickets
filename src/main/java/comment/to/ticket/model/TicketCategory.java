package comment.to.ticket.model;

public enum TicketCategory {

        BUG,
        FEATURE,
        BILLING,
        ACCOUNT,
        OTHER;

        public static TicketCategory from(String value) {
                return TicketCategory.valueOf(value.trim().toUpperCase());
        }
}
