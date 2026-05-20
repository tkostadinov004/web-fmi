package bg.sofia.uni.fmi.issuetracker.exception.project;

public class UnauthorizedProjectModificationException extends RuntimeException {
    public UnauthorizedProjectModificationException(String message) {
        super(message);
    }

    public UnauthorizedProjectModificationException(String message, Throwable cause) {
        super(message);
    }
}
