package bg.sofia.uni.fmi.issuetracker.exception.project;

public class UserNotPartOfProjectException extends RuntimeException {
    public UserNotPartOfProjectException(String message) {
        super(message);
    }

    public UserNotPartOfProjectException(String message, Throwable cause) {
        super(message, cause);
    }
}
