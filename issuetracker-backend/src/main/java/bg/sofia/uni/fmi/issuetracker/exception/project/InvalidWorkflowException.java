package bg.sofia.uni.fmi.issuetracker.exception.project;

public class InvalidWorkflowException extends RuntimeException {
    public InvalidWorkflowException(String message) {
        super(message);
    }

    public InvalidWorkflowException(String message, Throwable cause) {
        super(message, cause);
    }
}
