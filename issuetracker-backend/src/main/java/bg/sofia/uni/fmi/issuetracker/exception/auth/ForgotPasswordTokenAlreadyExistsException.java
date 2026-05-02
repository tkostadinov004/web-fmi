package bg.sofia.uni.fmi.issuetracker.exception.auth;

public class ForgotPasswordTokenAlreadyExistsException extends AuthException {
    public ForgotPasswordTokenAlreadyExistsException(String message) {
        super(message);
    }

    public ForgotPasswordTokenAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
