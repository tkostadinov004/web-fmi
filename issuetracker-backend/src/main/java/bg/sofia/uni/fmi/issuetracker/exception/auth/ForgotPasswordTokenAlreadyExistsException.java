package bg.sofia.uni.fmi.issuetracker.exception.auth;

import bg.sofia.uni.fmi.issuetracker.exception.AlreadyExistsException;

public class ForgotPasswordTokenAlreadyExistsException extends AlreadyExistsException {
    public ForgotPasswordTokenAlreadyExistsException(String message) {
        super(message);
    }

    public ForgotPasswordTokenAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
