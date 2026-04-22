package bg.sofia.uni.fmi.issuetracker.service.contract;

import bg.sofia.uni.fmi.issuetracker.dto.auth.UserLoginDTO;
import bg.sofia.uni.fmi.issuetracker.dto.auth.UserRegisterDTO;
import bg.sofia.uni.fmi.issuetracker.response.AuthResponse;

import bg.sofia.uni.fmi.issuetracker.exception.user.UserAlreadyExistsException;

public interface AuthService {
    /**
     * Registers a user in the system.
     *
     * @param user the user to be added
     * @throws UserAlreadyExistsException if a user with the provided username already exists
     * @return {@link AuthResponse}
     */
    AuthResponse register(UserRegisterDTO user);

    AuthResponse login(UserLoginDTO user);
}
