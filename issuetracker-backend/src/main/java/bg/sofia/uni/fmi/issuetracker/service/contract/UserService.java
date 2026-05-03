package bg.sofia.uni.fmi.issuetracker.service.contract;

import bg.sofia.uni.fmi.issuetracker.dto.input.UpdateUserDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.AdminOnlyOutputUserDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.UserDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    /**
     * Checks whether the given user has administrator privileges.
     *
     * @param username the username to check
     * @return {@code true} if the user is an admin, {@code false} otherwise
     * @throws UserNotFoundException if no user with the given username exists
     */
    boolean isAdmin(String username);

    /**
     * Marks the user as deleted.
     *
     * <p>Note that the user remains in the database, but is flagged as deleted and can no longer access the account.</p>
     *
     * @param username the username of the user to delete
     * @throws UserNotFoundException if the user does not exist or is already marked deleted
     */
    void deleteUser(String username);

    /**
     * Checks whether the specified user is marked as deleted.
     *
     * @param username the username to check
     * @return {@code true} if the user is marked deleted, {@code false} otherwise
     * @throws UserNotFoundException if no user with the given username exists
     */
    boolean isDeleted(String username);

    /**
     * Saves or replaces the user's profile picture file and updates the stored profile picture path.
     *
     * @param username the username whose profile picture is being updated
     * @param picture  the uploaded picture file
     * @throws UserNotFoundException if the user does not exist
     */
    void setProfilePicture(String username, MultipartFile picture);

    /**
     * Retrieves the user details for the given username.
     *
     * @param username the username to load
     * @return a {@link UserDetailsDTO} containing profile information, roles, and project data
     * @throws UserNotFoundException if no user with the given username exists
     */
    UserDetailsDTO getUser(String username);

    /**
     * Returns a paginated list of users with admin-only details.
     *
     * @param pageNumber the page number to return, starting at 1; values less than or equal to 0 use the default page number
     * @param pageSize   the page size to return; values less than or equal to 0 use the default page size
     * @param orderBy    the field to sort by
     * @param ascending  whether to sort in ascending order
     * @return a {@link Page} of {@link AdminOnlyOutputUserDTO} objects
     */
    Page<AdminOnlyOutputUserDTO> getAllUsers(int pageNumber, int pageSize, String orderBy, boolean ascending);

    /**
     * Updates the user's profile information from the provided DTO.
     *
     * @param username the username of the user to update
     * @param dto      the data to apply to the user
     * @throws UserNotFoundException      if the user does not exist
     * @throws UserAlreadyExistsException if the new email is already registered to another user
     */
    void updateUser(String username, UpdateUserDTO dto);
}
