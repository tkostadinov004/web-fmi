package bg.sofia.uni.fmi.issuetracker.exception.project;

public class ProjectUserAlreadyInProjectException extends RuntimeException {
    public ProjectUserAlreadyInProjectException(String message) {
        super(message);
    }
    public ProjectUserAlreadyInProjectException(String message, Throwable cause) {
        super(message, cause);
    }
}
