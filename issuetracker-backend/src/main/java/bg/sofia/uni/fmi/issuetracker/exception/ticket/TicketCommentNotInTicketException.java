package bg.sofia.uni.fmi.issuetracker.exception.ticket;

public class TicketCommentNotInTicketException extends RuntimeException {
    public TicketCommentNotInTicketException(String message) {
        super(message);
    }

    public TicketCommentNotInTicketException(String message, Throwable cause) {
        super(message, cause);
    }
}
