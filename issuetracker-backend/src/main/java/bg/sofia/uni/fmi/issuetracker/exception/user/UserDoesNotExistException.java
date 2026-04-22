package bg.sofia.uni.fmi.issuetracker.exception.user;

import bg.sofia.uni.fmi.issuetracker.exception.NotFoundException;

public class UserDoesNotExistException extends NotFoundException {
    public UserDoesNotExistException(String message) {
        super(message);
    }

    public UserDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
