package bg.sofia.uni.fmi.issuetracker.exception.project;

import bg.sofia.uni.fmi.issuetracker.exception.AlreadyExistsException;

public class WorkflowAlreadyExistsException extends AlreadyExistsException {
    public WorkflowAlreadyExistsException(String message) {
        super(message);
    }

    public WorkflowAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
