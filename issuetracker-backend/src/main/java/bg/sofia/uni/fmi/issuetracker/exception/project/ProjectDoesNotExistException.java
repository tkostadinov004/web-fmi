package bg.sofia.uni.fmi.issuetracker.exception.project;

public class ProjectDoesNotExistException extends RuntimeException {
    public ProjectDoesNotExistException(String message) {
        super(message);
    }

    public ProjectDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
