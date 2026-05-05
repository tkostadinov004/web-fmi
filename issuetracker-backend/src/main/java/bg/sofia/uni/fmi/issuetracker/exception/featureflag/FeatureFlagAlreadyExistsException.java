package bg.sofia.uni.fmi.issuetracker.exception.featureflag;

import bg.sofia.uni.fmi.issuetracker.exception.AlreadyExistsException;

public class FeatureFlagAlreadyExistsException extends AlreadyExistsException {
    public FeatureFlagAlreadyExistsException(String message) {
        super(message);
    }

    public FeatureFlagAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
