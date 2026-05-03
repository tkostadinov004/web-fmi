package bg.sofia.uni.fmi.issuetracker.utils.messages;

public class ValidationConstants {
    public static class Auth {
        public static final String BLANK_FIRST_NAME = "First name should not be blank or empty!";
        public static final String BLANK_LAST_NAME = "Last name should not be blank or empty!";
        public static final String BLANK_USERNAME = "Username should not be blank or empty!";
        public static final String BLANK_EMAIL = "Email should not be blank or empty!";
        public static final String BLANK_PASSWORD = "Password should not be blank or empty!";
        public static final String BLANK_REDIRECT_URL = "Redirect URL should not be blank or empty!";

        public static final String LENGTH_FIRST_NAME = "First name should be at most 200 characters long!";
        public static final String LENGTH_LAST_NAME = "Last name should be at most 200 characters long!";
        public static final String LENGTH_USERNAME = "Username should be at most 100 characters long!";
        public static final String LENGTH_EMAIL = "Email should be at most 255 characters long!";
        public static final String LENGTH_COMPANY_NAME = "Company name should be at most 200 characters long!";
        public static final String INVALID_EMAIL = "Invalid email!";
    }

    private static final String MISSING_PARAM = "Missing parameter: '%s'";

    public static String missingParam(String paramName) {
        return MISSING_PARAM.formatted(paramName);
    }
}
