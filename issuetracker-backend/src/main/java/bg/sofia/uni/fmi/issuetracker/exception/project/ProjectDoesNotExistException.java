package bg.sofia.uni.fmi.issuetracker.exception.project;

import bg.sofia.uni.fmi.issuetracker.exception.NotFoundException;

public class ProjectDoesNotExistException extends NotFoundException {
    public ProjectDoesNotExistException(String message) {
        super(message);
    }

    public ProjectDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
