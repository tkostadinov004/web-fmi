package bg.sofia.uni.fmi.issuetracker.exception.ticket;

import bg.sofia.uni.fmi.issuetracker.exception.NotFoundException;

public class TicketNotFoundException extends NotFoundException {
    public TicketNotFoundException(String message) {
        super(message);
    }

    public TicketNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
