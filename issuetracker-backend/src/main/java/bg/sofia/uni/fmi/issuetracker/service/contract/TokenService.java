package bg.sofia.uni.fmi.issuetracker.service.contract;

import bg.sofia.uni.fmi.issuetracker.model.auth.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface TokenService {
    /**
     * Checks if a token is valid. A token is valid if all of the above are satisfied:
     * <ul>
     *     <li>the token exists in the database</li>
     *     <li>the token is owned by the provided user</li>
     *     <li>the token is not expired</li>
     * </ul>
     * @param token the token
     * @param user the owner of the token
     * @return {@code true} if the token is valid, {@code false} otherwise
     */
    boolean isValid(String token, User user);
}
