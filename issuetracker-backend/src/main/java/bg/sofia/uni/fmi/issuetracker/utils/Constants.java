package bg.sofia.uni.fmi.issuetracker.utils;

import java.util.HashSet;
import java.util.Set;

public class Constants {
    public static final String FORGOT_PASSWORD_EMAIL_SUBJECT = "Forgot password - Issuetracker";
    public static final long MAX_IMAGE_FILE_SIZE = 10_000_000; // 10 MB = 10 million bytes
    public static final String JWT_KEY_ENV_PATH = "services.auth.jwt_private_key";
    public static final long DEFAULT_AUTH_TOKEN_VALIDITY = 24 * 60 * 60 * 1000; // 24 hours
    public static final long DEFAULT_FORGOT_PASSWORD_TOKEN_VALIDITY = 5 * 60 * 1000; // 5 minutes
    public static final String DEFAULT_PAGE_NUMBER = "1";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String SKIP_EMAIL_FEATURE_FLAG = "SKIP_EMAIL";

    public static final Set<String> VALID_IMAGE_FORMATS = new HashSet<>(Set.of(
            "jpeg", "jpg", "png"
    ));
}
