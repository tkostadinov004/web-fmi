package bg.sofia.uni.fmi.issuetracker.exception.ticket;

public class UnassignedTicketException extends RuntimeException {
    public UnassignedTicketException(String message) {
        super(message);
    }

    public UnassignedTicketException(String message, Throwable cause) {
        super(message, cause);
    }
}
