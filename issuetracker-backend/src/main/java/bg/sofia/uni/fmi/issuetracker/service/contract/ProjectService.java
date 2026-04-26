package bg.sofia.uni.fmi.issuetracker.service.contract;

import bg.sofia.uni.fmi.issuetracker.model.auth.Role;

import java.util.Collection;

public interface ProjectService {
    /**
     * Checks whether the given user is a member of the project
     *
     * @param username  the username of the user
     * @param projectId the ID of the project
     * @return {@code true} if the user is a member of the team, {@code false} otherwise
     */
    boolean isMemberOf(String username, String projectId);

    /**
     * Checks whether the given user has given roles in a given project.
     * The main logic is driven by the {@code strict} parameter as follows: </br>
     * If {@code strict is true}, all roles in the collection would be required for the given user.
     * In other words, the method will return true if the user has each of the provided roles in the project.
     * </br>
     * Otherwise, the method will return true if the user has at least 1 of the provided roles.
     *
     * @param username  the username of the user
     * @param projectId the ID of the project
     * @param roles     a collection of roles to be checked
     */
    boolean hasRoles(String username, String projectId, Collection<Role> roles, boolean strict);
}
