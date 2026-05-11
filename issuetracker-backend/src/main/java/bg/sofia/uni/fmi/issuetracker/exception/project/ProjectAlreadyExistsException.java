package bg.sofia.uni.fmi.issuetracker.exception.project;

public class ProjectAlreadyExistsException extends RuntimeException {
    public ProjectAlreadyExistsException(String message) {
        super(message);
    }

    public ProjectAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
