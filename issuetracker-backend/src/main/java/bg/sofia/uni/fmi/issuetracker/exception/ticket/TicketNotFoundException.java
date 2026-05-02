package bg.sofia.uni.fmi.issuetracker.exception.ticket;

public class TicketNotFoundException extends RuntimeException {
    public TicketNotFoundException(String message) {
        super(message);
    }

    public TicketNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
