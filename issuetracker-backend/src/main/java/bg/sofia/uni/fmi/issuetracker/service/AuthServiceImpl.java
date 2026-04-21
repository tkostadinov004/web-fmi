package bg.sofia.uni.fmi.issuetracker.service;

import bg.sofia.uni.fmi.issuetracker.dto.auth.UserLoginDTO;
import bg.sofia.uni.fmi.issuetracker.dto.auth.UserRegisterDTO;
import bg.sofia.uni.fmi.issuetracker.exception.auth.UserAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.exception.auth.UserAlreadyLoggedException;
import bg.sofia.uni.fmi.issuetracker.exception.auth.WrongCredentialsException;
import bg.sofia.uni.fmi.issuetracker.model.auth.Token;
import bg.sofia.uni.fmi.issuetracker.model.auth.User;
import bg.sofia.uni.fmi.issuetracker.repository.TokenRepository;
import bg.sofia.uni.fmi.issuetracker.repository.UserRepository;
import bg.sofia.uni.fmi.issuetracker.response.AuthResponse;
import bg.sofia.uni.fmi.issuetracker.service.contract.AuthService;
import bg.sofia.uni.fmi.issuetracker.service.contract.TokenService;
import bg.sofia.uni.fmi.issuetracker.utils.JwtUtils;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import bg.sofia.uni.fmi.issuetracker.utils.messages.OutputMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtUtils jwtUtils;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, TokenRepository tokenRepository, JwtUtils jwtUtils, TokenService tokenService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.jwtUtils = jwtUtils;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponse register(UserRegisterDTO user) {
        if (userRepository.existsById(user.username())) {
            throw new UserAlreadyExistsException(ExceptionMessages.Auth.userAlreadyExists(user.username()));
        }

        User newUser = new User(user.firstName(), user.lastName(), user.username(), passwordEncoder.encode(user.password()));
        newUser = userRepository.save(newUser);

        return new AuthResponse(OutputMessages.Auth.SUCCESSFULLY_CREATED_USER, null);
    }

    @Override
    public AuthResponse login(UserLoginDTO user) {
        Optional<User> foundUser = userRepository.findById(user.username());
        if (foundUser.isEmpty() || !passwordEncoder.matches(user.password(), foundUser.get().getPassword())) {
            throw new WrongCredentialsException(ExceptionMessages.Auth.wrongCredentials());
        }

        List<Token> tokens = tokenRepository.findAllByUser(foundUser.get());
        if (tokens.stream().anyMatch(token -> tokenService.isValid(token.getTokenValue(), foundUser.get()))) {
            throw new UserAlreadyLoggedException(ExceptionMessages.Auth.userAlreadyLoggedIn(user.username()));
        }
        tokenRepository.deleteAll(tokens);

        Token token = createToken(foundUser.get());
        return new AuthResponse(OutputMessages.Auth.SUCCESSFULLY_LOGGED_USER, token.getTokenValue());
    }

    Token createToken(User user) {
        String generatedToken = jwtUtils.generateToken(user);
        Token token = new Token(generatedToken, user);
        return tokenRepository.save(token);
    }
}
