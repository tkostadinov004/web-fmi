package bg.sofia.uni.fmi.issuetracker.exception.featureflag;

import bg.sofia.uni.fmi.issuetracker.exception.NotFoundException;

public class FeatureFlagNotFoundException extends NotFoundException {
    public FeatureFlagNotFoundException(String message) {
        super(message);
    }

    public FeatureFlagNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
