package bg.sofia.uni.fmi.issuetracker.exception.user;

import bg.sofia.uni.fmi.issuetracker.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
