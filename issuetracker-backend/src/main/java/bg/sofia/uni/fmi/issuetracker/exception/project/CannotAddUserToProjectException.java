package bg.sofia.uni.fmi.issuetracker.exception.project;

public class CannotAddUserToProjectException extends RuntimeException {
    public CannotAddUserToProjectException(String message) {
        super(message);
    }

    public CannotAddUserToProjectException(String message, Throwable cause) {
        super(message);
    }
}
