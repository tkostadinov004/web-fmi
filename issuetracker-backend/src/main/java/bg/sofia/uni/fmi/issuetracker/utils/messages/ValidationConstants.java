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

    public static class Ticket {
        public static final String BLANK_CODE = "Ticket code should not be blank!";
        public static final String BLANK_TITLE = "Ticket title should not be blank!";
        public static final String NULL_PRIORITY = "You should provide a ticket priority!";

        public static final String LENGTH_CODE = "Ticket code should be at most 100 characters long!";
        public static final String LENGTH_TITLE = "Ticket title should be at most 100 characters long!";
        public static final String LENGTH_DESCRIPTION = "Ticket description should be at most 500 characters long!";

        public static final String DUE_DATE_IN_THE_PAST = "The provided ticket due date is in the past!";
    }

    public static class TicketComment {
        public static final String BLANK_CONTENT = "Comment content should not be blank!";
    }

    public static class FeatureFlag {
        public static final String BLANK_NAME = "Feature flag name should not be blank!";
        public static final String NULL_VALUE = "Feature flag value should not be null!";
        public static final String INVALID_VALUE = "Feature flag value should be either 'true' or 'false'!";
        public static final String LENGTH_NAME = "Feature flag name should be at most 255 characters long!";
    }

    public static class File {
        public static final String BLANK_PATH = "Path should not be blank!";
    }
}
