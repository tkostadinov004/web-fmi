package bg.sofia.uni.fmi.issuetracker.exception.project;

import bg.sofia.uni.fmi.issuetracker.exception.NotFoundException;

public class ProjectUserNotFoundException extends NotFoundException {
    public ProjectUserNotFoundException(String message) {
        super(message);
    }

    public ProjectUserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
