package bg.sofia.uni.fmi.issuetracker.service.contract;

import bg.sofia.uni.fmi.issuetracker.dto.output.AdminOnlyOutputUserDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.UserDetailsDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    /**
     * Checks whether the given user is an admin.
     *
     * @param username the username of the user
     * @return {@code true} if the given user is an admin, {@code false} otherwise
     */
    boolean isAdmin(String username);

    /**
     * Marks the user as deleted. Note that the user is not deleted from the database, but
     * rather marked as deleted, meaning they won't be able to access their account anymore.
     *
     * @param username
     */
    void deleteUser(String username);

    /**
     * Checks whether the given user is marked as deleted.
     *
     * @param username the username of the user
     * @return {@code true} if the user is marked as deleted, {@code false} otherwise
     */
    boolean isDeleted(String username);

    void setProfilePicture(String username, MultipartFile picture);

    UserDetailsDTO getUser(String username);

    Page<AdminOnlyOutputUserDTO> getAllUsers(int pageNumber, int pageSize, String orderBy, boolean ascending);
}
