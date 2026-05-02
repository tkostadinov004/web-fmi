package bg.sofia.uni.fmi.issuetracker.exception.auth;

public class AlreadyChangedPasswordException extends AuthException {
    public AlreadyChangedPasswordException(String message) {
        super(message);
    }

    public AlreadyChangedPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
