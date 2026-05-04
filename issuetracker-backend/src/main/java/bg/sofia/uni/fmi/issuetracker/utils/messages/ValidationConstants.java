package bg.sofia.uni.fmi.issuetracker.utils.messages;

public class ValidationConstants {
    public class Auth {
        public static final String BLANK_FIRST_NAME = "First name should not be blank or empty!";
        public static final String BLANK_LAST_NAME = "Last name should not be blank or empty!";
        public static final String BLANK_USERNAME = "Username should not be blank or empty!";
        public static final String BLANK_PASSWORD = "Password should not be blank or empty!";
    }

    public static class Ticket {
        public static final String BLANK_TITLE = "Ticket title should not be blank!";
        public static final String NULL_PRIORITY = "Ticket priority must not be null!";
    }

    public static class TicketComment {
        public static final String BLANK_CONTENT = "Comment content should not be blank!";
    }
}
