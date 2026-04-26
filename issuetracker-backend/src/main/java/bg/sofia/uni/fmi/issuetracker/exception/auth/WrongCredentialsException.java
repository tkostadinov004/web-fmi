package bg.sofia.uni.fmi.issuetracker.exception.auth;

public class WrongCredentialsException extends AuthException {
    public WrongCredentialsException(String message) {
        super(message);
    }

    public WrongCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
