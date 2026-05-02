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

        public static String projectNotFound(String projectId) {
            return PROJECT_NOT_FOUND.formatted(projectId);
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
    }
}
