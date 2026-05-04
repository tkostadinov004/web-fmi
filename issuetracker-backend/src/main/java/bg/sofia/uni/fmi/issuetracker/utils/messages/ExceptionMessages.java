package bg.sofia.uni.fmi.issuetracker.utils.messages;

public class ExceptionMessages {
    public static class Auth {
        private static final String WRONG_CREDENTIALS = "Wrong username or password!";

        public static String wrongCredentials() {
            return WRONG_CREDENTIALS;
        }

        private static final String USER_ALREADY_LOGGED_IN = "User with username %s is already logged in!";

        public static String userAlreadyLoggedIn(String username) {
            return USER_ALREADY_LOGGED_IN.formatted(username);
        }

        private static final String USER_NOT_LOGGED = "User not logged in!";

        public static String userNotLogged() {
            return USER_NOT_LOGGED;
        }
    }

    public static class Project {
        private static final String PROJECT_NOT_FOUND = "Project with UUID %s is not found!";
        private static final String USER_NOT_IN_PROJECT = "User %s is not part of project %s!";

        public static String projectNotFound(String projectId) {
            return PROJECT_NOT_FOUND.formatted(projectId);
        }

        public static String userNotInProject(String username, String projectId) {
            return USER_NOT_IN_PROJECT.formatted(username, projectId);
        }
    }

    public static class User {
        private static final String USER_ALREADY_EXISTS = "User with username %s already exists!";

        public static String userAlreadyExists(String username) {
            return USER_ALREADY_EXISTS.formatted(username);
        }

        private static final String USER_NOT_FOUND = "User with username %s not found!";

        public static String userNotFound(String username) {
            return USER_NOT_FOUND.formatted(username);
        }
    }

    public static class Ticket {
        private static final String TICKET_ALREADY_EXISTS = "Ticket with UUID %s already exists!";
        private static final String TICKET_NOT_FOUND = "Ticket with UUID %s not found!";
        private static final String TICKET_NOT_IN_PROJECT =
            "Ticket with UUID %s does not belong to project with UUID %s!";

        public static String ticketAlreadyExists(String ticketUuid) {
            return TICKET_ALREADY_EXISTS.formatted(ticketUuid);
        }

        public static String ticketNotFound(String ticketUuid) {
            return TICKET_NOT_FOUND.formatted(ticketUuid);
        }

        public static String ticketNotInProject(String ticketUuid, String projectUuid) {
            return TICKET_NOT_IN_PROJECT.formatted(ticketUuid, projectUuid);
        }
    }

    public static class TicketComment {
        private static final String COMMENT_ALREADY_EXISTS = "Ticket comment with UUID %s already exists!";
        private static final String COMMENT_NOT_FOUND = "Ticket comment with UUID %s not found!";
        private static final String COMMENT_NOT_IN_TICKET =
            "Ticket comment with UUID %s does not belong to ticket with UUID %s!";

        public static String ticketCommentAlreadyExists(String commentUuid) {
            return COMMENT_ALREADY_EXISTS.formatted(commentUuid);
        }

        public static String ticketCommentNotFound(String commentUuid) {
            return COMMENT_NOT_FOUND.formatted(commentUuid);
        }

        public static String ticketCommentNotInTicket(String commentUuid, String ticketUuid) {
            return COMMENT_NOT_IN_TICKET.formatted(commentUuid, ticketUuid);
        }
    }
}
