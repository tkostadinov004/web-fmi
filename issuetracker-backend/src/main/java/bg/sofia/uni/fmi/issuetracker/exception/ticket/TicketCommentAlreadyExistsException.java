package bg.sofia.uni.fmi.issuetracker.exception.ticket;

import bg.sofia.uni.fmi.issuetracker.exception.AlreadyExistsException;

public class TicketCommentAlreadyExistsException extends AlreadyExistsException {
    public TicketCommentAlreadyExistsException(String message) {
        super(message);
    }

    public TicketCommentAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
