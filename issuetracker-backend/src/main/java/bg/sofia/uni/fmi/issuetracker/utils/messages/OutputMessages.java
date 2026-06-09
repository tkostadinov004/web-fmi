package bg.sofia.uni.fmi.issuetracker.utils.messages;

public class OutputMessages {
    public static class Auth {
        public static final String SUCCESSFULLY_CREATED_USER = "Successfully created user!";
        public static final String SUCCESSFULLY_LOGGED_USER = "Successfully logged in!";
    }

    public static class System {
        public static final String UNEXPECTED_SERVER_ERROR = "Unexpected server error!";
        public static final String ACCESS_DENIED = "Access denied!";
        public static final String UNAUTHORIZED = "Invalid or expired token provided!";
    }
}
