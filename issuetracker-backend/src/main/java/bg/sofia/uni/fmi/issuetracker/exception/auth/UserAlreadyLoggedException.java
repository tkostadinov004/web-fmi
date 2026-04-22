package bg.sofia.uni.fmi.issuetracker.exception.auth;

public class UserAlreadyLoggedException extends AuthException {
    public UserAlreadyLoggedException(String message) {
        super(message);
    }

    public UserAlreadyLoggedException(String message, Throwable cause) {
        super(message, cause);
    }
}
