package bg.sofia.uni.fmi.issuetracker.exception.ticket;

public class TicketNotInProjectException extends RuntimeException {
    public TicketNotInProjectException(String message) {
        super(message);
    }

    public TicketNotInProjectException(String message, Throwable cause) {
        super(message, cause);
    }
}
