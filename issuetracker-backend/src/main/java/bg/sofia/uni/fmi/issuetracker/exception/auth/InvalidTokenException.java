package bg.sofia.uni.fmi.issuetracker.exception.auth;

public class InvalidTokenException extends AuthException {
    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
