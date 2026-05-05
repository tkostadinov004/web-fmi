package bg.sofia.uni.fmi.issuetracker.exception.project;

import bg.sofia.uni.fmi.issuetracker.exception.NotFoundException;

public class ProjectNotFoundException extends NotFoundException {
    public ProjectNotFoundException(String message) {
        super(message);
    }

    public ProjectNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
