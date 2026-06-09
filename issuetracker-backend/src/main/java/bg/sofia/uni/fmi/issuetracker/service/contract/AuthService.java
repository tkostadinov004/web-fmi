package bg.sofia.uni.fmi.issuetracker.service.contract;

import bg.sofia.uni.fmi.issuetracker.dto.input.auth.ChangeForgottenPasswordDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.auth.ChangePasswordDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.auth.SendForgotPasswordEmailDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.auth.UserLoginDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.auth.UserRegisterDTO;
import bg.sofia.uni.fmi.issuetracker.exception.auth.AlreadyChangedPasswordException;
import bg.sofia.uni.fmi.issuetracker.exception.auth.ForgotPasswordTokenAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.exception.auth.UserAlreadyLoggedException;
import bg.sofia.uni.fmi.issuetracker.exception.auth.WrongCredentialsException;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserNotFoundException;
import bg.sofia.uni.fmi.issuetracker.response.AuthResponse;

public interface AuthService {
    /**
     * Registers a new user in the system.
     *
     * <p>A new {@code User} entity with encoded password is created and saved
     * to the repository.</p>
     *
     * @param user the registration information for the new user
     * @return an {@link AuthResponse} containing a success message and no token
     * @throws UserAlreadyExistsException if a user with the provided username already exists
     */
    AuthResponse register(UserRegisterDTO user);

    /**
     * Authenticates the user and issues a new authentication token.
     *
     * <p>The username and password are verified, any old auth tokens are deleted, and
     * a new auth token is created.</p>
     *
     * @param user the login credentials
     * @return an {@link String} containing the new access token
     * @throws WrongCredentialsException  if the username does not exist or the password is incorrect
     * @throws UserAlreadyLoggedException if the user already has a valid active auth token
     */
    String login(UserLoginDTO user);

    /**
     * Logs the user out by deleting all authentication tokens for the given user.
     *
     * @param username the username of the user to log out
     * @throws UserNotFoundException if the user cannot be found
     */
    void logout(String username);

    /**
     * Changes the password for an authenticated user.
     *
     * <p>The implementation verifies that the user exists and is not deleted, checks that the
     * provided old password matches the stored password, and validates that the new password and
     * confirmation password match.</p>
     *
     * @param username          the username whose password is being changed
     * @param changePasswordDTO the current and new password values
     * @throws UserNotFoundException     if the user does not exist or is marked as deleted
     * @throws WrongCredentialsException if the old password is incorrect or the new passwords do not match
     */
    void changePassword(String username, ChangePasswordDTO changePasswordDTO);

    /**
     * Sends a forgot-password email to the user.
     *
     * <p>The implementation verifies that the user exists, that the email matches the user record,
     * and that there is not already a valid forgot-password token before sending the reset email.</p>
     *
     * @param dto the forgot-password email request data, including email address and redirect URL
     * @return the token used for resetting the password
     * @throws UserNotFoundException                     if the user does not exist or is marked as deleted
     * @throws ForgotPasswordTokenAlreadyExistsException if a valid reset token already exists for the user
     */
    String sendForgotPasswordEmail(SendForgotPasswordEmailDTO dto);

    /**
     * Changes the user's forgotten password using a valid token.
     *
     * <p>The implementation verifies that the user exists, that the new password values match,
     * and that the provided token is valid and corresponds to an existing forgot-password token.
     * After a successful password reset the token is deleted.</p>
     *
     * @param dto the forgotten password change request including token and new password values
     * @throws UserNotFoundException           if the user does not exist or is marked as deleted
     * @throws WrongCredentialsException       if the new passwords do not match
     * @throws AlreadyChangedPasswordException if the token is invalid or no matching forgot-password token exists
     */
    void changeForgottenPassword(ChangeForgottenPasswordDTO dto);
}
