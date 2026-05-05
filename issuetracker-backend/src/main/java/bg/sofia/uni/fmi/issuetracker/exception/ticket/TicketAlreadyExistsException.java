package bg.sofia.uni.fmi.issuetracker.exception.ticket;

import bg.sofia.uni.fmi.issuetracker.exception.AlreadyExistsException;

public class TicketAlreadyExistsException extends AlreadyExistsException {
    public TicketAlreadyExistsException(String message) {
        super(message);
    }

    public TicketAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
