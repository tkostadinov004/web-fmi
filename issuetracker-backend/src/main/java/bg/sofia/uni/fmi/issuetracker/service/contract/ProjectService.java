package bg.sofia.uni.fmi.issuetracker.service.contract;

import bg.sofia.uni.fmi.issuetracker.model.auth.User;
import org.springframework.security.core.Authentication;

public interface ProjectService {
    /**
     * Checks whether the given user is a member of the project
     * @param user the user
     * @param projectId the ID of the project
     * @return {@code true} if the user is a member of the team, {@code false} otherwise
     */
    boolean isMemberOf(User user, String projectId);

    /**
     * Checks whether the given user has the given role in the project
     * @param authentication the current authentication principal
     * @param projectId the ID of the project
     * @param roleString the name of the desired role
     * @return {@code true} if the user has the given role in the project, {@code false} otherwise
     */
    boolean hasRole(Authentication authentication, String projectId, String roleString);
}
