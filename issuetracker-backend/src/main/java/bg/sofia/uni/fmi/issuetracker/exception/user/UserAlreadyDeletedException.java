package bg.sofia.uni.fmi.issuetracker.exception.user;

public class UserAlreadyDeletedException extends RuntimeException {
    public UserAlreadyDeletedException(String message) {
        super(message);
    }

    public UserAlreadyDeletedException(String message, Throwable cause) {
        super(message, cause);
    }
}
