package bg.sofia.uni.fmi.issuetracker.exception.ticket;

import bg.sofia.uni.fmi.issuetracker.exception.NotFoundException;

public class TicketCommentNotFoundException extends NotFoundException {
    public TicketCommentNotFoundException(String message) {
        super(message);
    }

    public TicketCommentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
