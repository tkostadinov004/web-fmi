package bg.sofia.uni.fmi.issuetracker.exception.ticket;

public class TicketCommentAlreadyExistsException extends RuntimeException {
    public TicketCommentAlreadyExistsException(String message) {
        super(message);
    }

    public TicketCommentAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
