package bg.sofia.uni.fmi.issuetracker.exception.ticket;

public class TicketCommentNotFoundException extends RuntimeException {
    public TicketCommentNotFoundException(String message) {
        super(message);
    }

    public TicketCommentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
