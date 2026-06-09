package bg.sofia.uni.fmi.issuetracker.exception.project;

import bg.sofia.uni.fmi.issuetracker.exception.NotFoundException;

public class WorkflowNotFoundException extends NotFoundException {
    public WorkflowNotFoundException(String message) {
        super(message);
    }

    public WorkflowNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
