package bg.sofia.uni.fmi.issuetracker.utils.messages;

import bg.sofia.uni.fmi.issuetracker.model.auth.Role;
import bg.sofia.uni.fmi.issuetracker.utils.Constants;

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

        private static final String SIZE_EXCEEDED = "The provided file must be less than %s megabytes!";

        public static String sizeExceeded() {
            return SIZE_EXCEEDED.formatted(Constants.MAX_IMAGE_FILE_SIZE / 1_000_000); // dividing by 1 MB
        }

        private static final String INVALID_FORMAT = "The provided file must have one of the following extensions: %s!";

        public static String invalidFormat() {
            return INVALID_FORMAT.formatted(String.join(", ", Constants.VALID_IMAGE_FORMATS));
        }

        private static final String INVALID_CUSTOM_NAME = "File name contains invalid characters!";

        public static String invalidCustomName() {
            return INVALID_CUSTOM_NAME;
        }
    }

    public static class Ticket {
        private static final String TICKET_ALREADY_EXISTS = "Ticket named '%s' already exists in project '%s'!";

        public static String ticketAlreadyExists(String code, String projectUuid) {
            return TICKET_ALREADY_EXISTS.formatted(code, projectUuid);
        }

        private static final String TICKET_NOT_FOUND = "Ticket '%s' not found!";

        public static String ticketNotFound(String code) {
            return TICKET_NOT_FOUND.formatted(code);
        }

        private static final String TICKET_PROJECT_MISMATCH =
                "Ticket '%s' and parent ticket '%s' cannot be from different projects!";

        public static String ticketProjectMismatch(String dependentCode, String parentCode) {
            return TICKET_PROJECT_MISMATCH.formatted(dependentCode, parentCode);
        }

        private static final String UNASSIGNED_TICKET =
                "Ticket '%s' is currently not assigned to anyone!";

        public static String unassignedTicket(String ticketCode) {
            return UNASSIGNED_TICKET.formatted(ticketCode);
        }
    }

    public static class TicketComment {
        private static final String COMMENT_ALREADY_EXISTS = "Ticket comment with UUID %s already exists!";

        public static String ticketCommentAlreadyExists(String commentUuid) {
            return COMMENT_ALREADY_EXISTS.formatted(commentUuid);
        }

        private static final String COMMENT_NOT_FOUND = "Ticket comment with UUID %s not found!";

        public static String ticketCommentNotFound(String commentUuid) {
            return COMMENT_NOT_FOUND.formatted(commentUuid);
        }

        private static final String COMMENT_NOT_IN_TICKET =
                "Ticket comment with UUID %s does not belong to ticket with title '%s'!";

        public static String ticketCommentNotInTicket(String commentUuid, String ticketTitle) {
            return COMMENT_NOT_IN_TICKET.formatted(commentUuid, ticketTitle);
        }

        private static final String ALLOWED_TO_MODIFY_ONLY_OWN_COMMENTS =
                "You're only allowed to modify your own comments!";

        public static String allowedToModifyOnlyOwnComments() {
            return ALLOWED_TO_MODIFY_ONLY_OWN_COMMENTS;
        }

        private static final String ALLOWED_TO_DELETE_ONLY_OWN_COMMENTS =
                "You're only allowed to delete your own comments!";

        public static String allowedToDeleteOnlyOwnComments() {
            return ALLOWED_TO_DELETE_ONLY_OWN_COMMENTS;
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

        private static final String USER_ALREADY_IN_PROJECT_ROLE =
                "User with username %s is already part of project %s with role %s!";

        private static final String USER_ALREADY_IN_PROJECT =
                "User with username %s is already part of project %s!";

        private static final String USER_NOT_FOUND = "User with username %s not found in project %s!";

        private static final String CANNOT_ADD_USER_TO_PROJECT = "Cannot add user to project '%s' because initiator '%s' is not a team lead in the project!";
        private static final String CANNOT_REMOVE_USER_FROM_PROJECT = "Cannot remove user from project '%s' because initiator '%s' is not a team lead in the project!";

        public static String userAlreadyInProject(String username, String projectId, Role role) {
            return USER_ALREADY_IN_PROJECT_ROLE.formatted(username, projectId, role);
        }

        public static String userAlreadyInProject(String username, String projectId) {
            return USER_ALREADY_IN_PROJECT.formatted(username, projectId);
        }

        public static String userNotFound(String username, String projectId) {
            return USER_NOT_FOUND.formatted(username, projectId);
        }

        public static String cannotAddUserToProject(String projectId, String initiatorUsername) {
            return CANNOT_ADD_USER_TO_PROJECT.formatted(projectId, initiatorUsername);
        }

        public static String cannotRemoveUserFromProject(String projectId, String initiatorUsername) {
            return CANNOT_REMOVE_USER_FROM_PROJECT.formatted(projectId, initiatorUsername);
        }
    }

    public static class Workflow {
        private static final String INVALID_STATUS = "Invalid status transition from status '%s' to status '%s'!";

        public static String invalidStatus(String oldStatus, String newStatus) {
            return INVALID_STATUS.formatted(oldStatus, newStatus);
        }
    }
}
