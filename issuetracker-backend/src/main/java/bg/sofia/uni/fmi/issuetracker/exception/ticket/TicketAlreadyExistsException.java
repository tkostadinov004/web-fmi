package bg.sofia.uni.fmi.issuetracker.exception.ticket;

public class TicketAlreadyExistsException extends RuntimeException {
    public TicketAlreadyExistsException(String message) {
        super(message);
    }

    public TicketAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
