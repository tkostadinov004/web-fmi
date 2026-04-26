package bg.sofia.uni.fmi.issuetracker.service.contract;

public interface UserService {
    /**
     * Checks whether the given user is an admin.
     *
     * @param username the username of the user
     * @return {@code true} if the given user is an admin, {@code false} otherwise
     */
    boolean isAdmin(String username);
}
