package bg.sofia.uni.fmi.issuetracker.utils.messages;

public class ExceptionMessages {
    private static final String INVALID_URL = "Invalid URL!";

    public static String invalidUrl() {
        return INVALID_URL;
    }

    public static class Auth {
        private static final String WRONG_CREDENTIALS = "Wrong username or password!";

        public static String wrongCredentials() {
            return WRONG_CREDENTIALS;
        }

        private static final String WRONG_OLD_PASSWORD = "Wrong old password!";

        public static String wrongOldPassword() {
            return WRONG_OLD_PASSWORD;
        }

        private static final String NEW_PASSWORDS_DO_NOT_MATCH = "The new password and the repeated new password don't match!";

        public static String newPasswordsDoNotMatch() {
            return NEW_PASSWORDS_DO_NOT_MATCH;
        }

        private static final String WRONG_EMAIL = "The provided email doesn't match the user's real email!";

        public static String wrongEmail() {
            return WRONG_EMAIL;
        }

        private static final String ALREADY_CHANGED_PASSWORD = "You already changed your password!";

        public static String alreadyChangedPassword() {
            return ALREADY_CHANGED_PASSWORD;
        }

        private static final String FORGOT_PASSWORD_TOKEN_EXISTS = "You already started a forgot password procedure!";

        public static String forgotPasswordTokenAlreadyExists() {
            return FORGOT_PASSWORD_TOKEN_EXISTS;
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
        private static final String PROJECT_ALREADY_EXISTS = "Project with UUID %s already exists!";

        public static String projectNotFound(String projectId) {
            return PROJECT_NOT_FOUND.formatted(projectId);
        }

        public static String userNotInProject(String username, String projectId) {
            return USER_NOT_IN_PROJECT.formatted(username, projectId);
        }

        public static String projectAlreadyExists(String projectId) {
            return PROJECT_ALREADY_EXISTS.formatted(projectId);
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

        private static final String EMAIL_ALREADY_EXISTS = "Account with email '%s' already exists!";

        public static String emailAlreadyExists(String email) {
            return EMAIL_ALREADY_EXISTS.formatted(email);
        }
    }

    public static class File {
        private static final String FILE_ALREADY_EXISTS = "File '%s' already exists!";

        public static String fileAlreadyExists(String path) {
            return FILE_ALREADY_EXISTS.formatted(path);
        }

        private static final String EMPTY_FILE = "The file you're trying to upload is empty!";

        public static String emptyFile() {
            return EMPTY_FILE;
        }

        private static final String CANNOT_STORE_OUTSIDE_ROOT = "Cannot store file outside current directory";

        public static String unableToStoreOutsideOfRoot() {
            return CANNOT_STORE_OUTSIDE_ROOT;
        }

        private static final String UNREADABLE_FILE = "Cannot read file '%s'";

        public static String unreadableFile(String file) {
            return UNREADABLE_FILE.formatted(file);
        }

        private static final String CANNOT_WRITE = "Cannot write in file '%s'";

        public static String cannotWrite(String file) {
            return CANNOT_WRITE.formatted(file);
        }

        private static final String INVALID_FILE = "Invalid or nonexistent file in request!";

        public static String invalidFile() {
            return INVALID_FILE;
        }
    }

    public static class Ticket {
        private static final String TICKET_ALREADY_EXISTS = "Ticket named '%s' already exists!";

        public static String ticketAlreadyExists(String code) {
            return TICKET_ALREADY_EXISTS.formatted(code);
        }

        private static final String TICKET_NOT_FOUND = "Ticket '%s' not found!";

        public static String ticketNotFound(String code) {
            return TICKET_NOT_FOUND.formatted(code);
        }

        private static final String TICKET_PROJECT_MISMATCH =
                "Ticket '%s' and parent ticket '%s' cannot be from different projects!";

        public static String ticketProjectMismatch(String parentCode, String dependentCode) {
            return TICKET_PROJECT_MISMATCH.formatted(parentCode, dependentCode);
        }
    }

    public static class TicketComment {
        private static final String COMMENT_ALREADY_EXISTS = "Ticket comment with UUID %s already exists!";
        private static final String COMMENT_NOT_FOUND = "Ticket comment with UUID %s not found!";
        private static final String COMMENT_NOT_IN_TICKET =
                "Ticket comment with UUID %s does not belong to ticket with title '%s'!";

        public static String ticketCommentAlreadyExists(String commentUuid) {
            return COMMENT_ALREADY_EXISTS.formatted(commentUuid);
        }

        public static String ticketCommentNotFound(String commentUuid) {
            return COMMENT_NOT_FOUND.formatted(commentUuid);
        }

        public static String ticketCommentNotInTicket(String commentUuid, String ticketTitle) {
            return COMMENT_NOT_IN_TICKET.formatted(commentUuid, ticketTitle);
        }
    }

    public static class FeatureFlag {
        private static final String FEATURE_FLAG_ALREADY_EXISTS = "Feature flag '%s' already exists!";
        private static final String FEATURE_FLAG_NOT_FOUND = "Feature flag '%s' not found!";

        public static String featureFlagAlreadyExists(String name) {
            return FEATURE_FLAG_ALREADY_EXISTS.formatted(name);
        }

        public static String featureFlagNotFound(String name) {
            return FEATURE_FLAG_NOT_FOUND.formatted(name);
        }
    }

    public static class ProjectUser {

        private static final String USER_ALREADY_IN_PROJECT =
            "User with username %s is already part of project %s!";

        private static final String USER_NOT_FOUND = "User with username %s not found in project %s!";

        public static String userAlreadyInProject(String username, String projectId) {
            return USER_ALREADY_IN_PROJECT.formatted(username, projectId);
        }

        public static String userNotFound(String username, String projectId) {
            return USER_NOT_FOUND.formatted(username, projectId);
        }
    }
}
