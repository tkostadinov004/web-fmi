package bg.sofia.uni.fmi.issuetracker.exception.user;

import bg.sofia.uni.fmi.issuetracker.exception.AlreadyExistsException;

public class UserAlreadyExistsException extends AlreadyExistsException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }

    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
