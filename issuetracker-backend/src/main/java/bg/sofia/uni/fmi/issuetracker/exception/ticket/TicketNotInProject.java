package bg.sofia.uni.fmi.issuetracker.exception.ticket;

public class TicketNotInProject extends RuntimeException {
    public TicketNotInProject(String message) {
        super(message);
    }

    public TicketNotInProject(String message, Throwable cause) {
        super(message, cause);
    }
}
