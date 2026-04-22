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
    }

    public static class Project {
        private static final String PROJECT_DOES_NOT_EXIST = "Project with UUID %s does not exist!";

        public static String projectDoesNotExist(String projectId) {
            return PROJECT_DOES_NOT_EXIST.formatted(projectId);
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
}
