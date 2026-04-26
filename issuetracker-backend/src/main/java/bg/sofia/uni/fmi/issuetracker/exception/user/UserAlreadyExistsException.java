package bg.sofia.uni.fmi.issuetracker.exception.user;

import bg.sofia.uni.fmi.issuetracker.exception.auth.AuthException;

public class UserAlreadyExistsException extends AuthException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }

    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
