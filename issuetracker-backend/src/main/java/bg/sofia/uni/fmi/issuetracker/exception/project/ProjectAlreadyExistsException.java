package bg.sofia.uni.fmi.issuetracker.exception.project;

import bg.sofia.uni.fmi.issuetracker.exception.AlreadyExistsException;

public class ProjectAlreadyExistsException extends AlreadyExistsException {
    public ProjectAlreadyExistsException(String message) {
        super(message);
    }

    public ProjectAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
