package bg.sofia.uni.fmi.issuetracker.exception.ticket;

public class TicketCommentNotInTickedException extends RuntimeException {
    public TicketCommentNotInTickedException(String message) {
        super(message);
    }

    public TicketCommentNotInTickedException(String message, Throwable cause) {
        super(message, cause);
    }
}
