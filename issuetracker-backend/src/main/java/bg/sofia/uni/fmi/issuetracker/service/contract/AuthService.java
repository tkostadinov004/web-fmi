package bg.sofia.uni.fmi.issuetracker.service.contract;

import bg.sofia.uni.fmi.issuetracker.dto.input.auth.ChangeForgottenPasswordDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.auth.ChangePasswordDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.auth.SendForgotPasswordEmailDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.auth.UserLoginDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.auth.UserRegisterDTO;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.response.AuthResponse;

public interface AuthService {
    /**
     * Registers a user in the system.
     *
     * @param user the user to be added
     * @return {@link AuthResponse}
     * @throws UserAlreadyExistsException if a user with the provided username already exists
     */
    AuthResponse register(UserRegisterDTO user);

    /**
     * Logs the user into the system.
     *
     * @param user the user
     * @return an object, containing a message and an access token
     */
    AuthResponse login(UserLoginDTO user);

    /**
     * Logs the user out of the system.
     *
     * @param username the username of the user
     */
    void logout(String username);

    void changePassword(String username, ChangePasswordDTO changePasswordDTO);

    void sendForgotPasswordEmail(String username, SendForgotPasswordEmailDTO dto);

    void changeForgottenPassword(String username, ChangeForgottenPasswordDTO dto);
}
