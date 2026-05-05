package bg.sofia.uni.fmi.issuetracker.service;

import bg.sofia.uni.fmi.issuetracker.dto.input.auth.ChangeForgottenPasswordDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.auth.ChangePasswordDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.auth.SendForgotPasswordEmailDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.auth.UserLoginDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.auth.UserRegisterDTO;
import bg.sofia.uni.fmi.issuetracker.exception.auth.AlreadyChangedPasswordException;
import bg.sofia.uni.fmi.issuetracker.exception.auth.AuthException;
import bg.sofia.uni.fmi.issuetracker.exception.auth.ForgotPasswordTokenAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.exception.auth.UserAlreadyLoggedException;
import bg.sofia.uni.fmi.issuetracker.exception.auth.WrongCredentialsException;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserNotFoundException;
import bg.sofia.uni.fmi.issuetracker.model.User;
import bg.sofia.uni.fmi.issuetracker.model.auth.Token;
import bg.sofia.uni.fmi.issuetracker.model.auth.TokenType;
import bg.sofia.uni.fmi.issuetracker.repository.TokenRepository;
import bg.sofia.uni.fmi.issuetracker.repository.UserRepository;
import bg.sofia.uni.fmi.issuetracker.response.AuthResponse;
import bg.sofia.uni.fmi.issuetracker.service.contract.AuthService;
import bg.sofia.uni.fmi.issuetracker.service.contract.FeatureFlagService;
import bg.sofia.uni.fmi.issuetracker.utils.Constants;
import bg.sofia.uni.fmi.issuetracker.utils.EmailUtils;
import bg.sofia.uni.fmi.issuetracker.utils.JwtUtils;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import bg.sofia.uni.fmi.issuetracker.utils.messages.OutputMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final EmailUtils emailUtils;
    private final FeatureFlagService featureFlagService;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, TokenRepository tokenRepository, JwtUtils jwtUtils, PasswordEncoder passwordEncoder, EmailUtils emailUtils, FeatureFlagService featureFlagService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.emailUtils = emailUtils;
        this.featureFlagService = featureFlagService;
    }

    @Override
    public AuthResponse register(UserRegisterDTO user) {
        if (userRepository.existsById(user.username())) {
            throw new UserAlreadyExistsException(ExceptionMessages.User.userAlreadyExists(user.username()));
        }

        User newUser = User.UserBuilder.newBuilder()
                .firstName(user.firstName())
                .lastName(user.lastName())
                .username(user.username())
                .email(user.email())
                .companyName(user.companyName())
                .password(passwordEncoder.encode(user.password()))
                .build();
        userRepository.save(newUser);

        return new AuthResponse(OutputMessages.Auth.SUCCESSFULLY_CREATED_USER, null);
    }

    @Override
    public AuthResponse login(UserLoginDTO user) {
        Optional<User> foundUser = userRepository.findById(user.username());
        if (foundUser.isEmpty() || !passwordEncoder.matches(user.password(), foundUser.get().getPassword())) {
            throw new WrongCredentialsException(ExceptionMessages.Auth.wrongCredentials());
        }

        List<Token> tokens = tokenRepository.findAllByUserAndTokenType(foundUser.get(), TokenType.AUTH);
        if (tokens.stream().anyMatch(token -> !jwtUtils.isTokenExpired(token.getTokenValue()))) {
            throw new UserAlreadyLoggedException(ExceptionMessages.Auth.userAlreadyLoggedIn(user.username()));
        }
        tokenRepository.deleteAll(tokens);

        Token token = createToken(foundUser.get(), TokenType.AUTH, Constants.DEFAULT_AUTH_TOKEN_VALIDITY);
        return new AuthResponse(OutputMessages.Auth.SUCCESSFULLY_LOGGED_USER, token.getTokenValue());
    }

    @Override
    @Transactional
    public void logout(String username) {
        Optional<User> user = userRepository.findById(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException(ExceptionMessages.User.userNotFound(username));
        }

        tokenRepository.deleteAllByUserAndTokenType(user.get(), TokenType.AUTH);
    }

    @Override
    @Transactional
    public void changePassword(String username, ChangePasswordDTO changePasswordDTO) {
        Optional<User> user = userRepository.findById(username);
        if (user.isEmpty() || user.get().isDeleted()) {
            throw new UserNotFoundException(ExceptionMessages.User.userNotFound(username));
        }
        if (!changePasswordDTO.newPassword().equals(changePasswordDTO.repeatedNewPassword())) {
            throw new WrongCredentialsException(ExceptionMessages.Auth.newPasswordsDoNotMatch());
        }
        if (!passwordEncoder.matches(changePasswordDTO.oldPassword(), user.get().getPassword())) {
            throw new WrongCredentialsException(ExceptionMessages.Auth.wrongOldPassword());
        }

        user.get().setPassword(passwordEncoder.encode(changePasswordDTO.newPassword()));
        userRepository.save(user.get());
    }

    @Override
    public String sendForgotPasswordEmail(String username, SendForgotPasswordEmailDTO dto) {
        Optional<User> user = userRepository.findById(username);
        if (user.isEmpty() || user.get().isDeleted()) {
            throw new UserNotFoundException(ExceptionMessages.User.userNotFound(username));
        }
        if (!user.get().getEmail().equals(dto.email())) {
            throw new AuthException(ExceptionMessages.Auth.wrongEmail());
        }

        List<Token> forgotPasswordTokens = tokenRepository.findAllByUserAndTokenType(user.get(), TokenType.FORGOT_PASSWORD);
        if (forgotPasswordTokens.stream().anyMatch(token -> jwtUtils.isValid(token.getTokenValue()))) {
            throw new ForgotPasswordTokenAlreadyExistsException(ExceptionMessages.Auth.forgotPasswordTokenAlreadyExists());
        }

        Token emailToken = createToken(user.get(), TokenType.FORGOT_PASSWORD, Constants.DEFAULT_FORGOT_PASSWORD_TOKEN_VALIDITY);

        Optional<String> shouldSkipEmailSending = featureFlagService.getFeatureFlagValueUnsafe("SKIP_EMAIL");
        if (shouldSkipEmailSending.isEmpty() || !Boolean.parseBoolean(shouldSkipEmailSending.get())) {
            emailUtils.sendForgotPasswordEmail(dto.email(), dto.redirectUrl(), emailToken);
        }

        return emailToken.getTokenValue();
    }

    @Override
    @Transactional
    public void changeForgottenPassword(String username, ChangeForgottenPasswordDTO dto) {
        Optional<User> user = userRepository.findById(username);
        if (user.isEmpty() || user.get().isDeleted()) {
            throw new UserNotFoundException(ExceptionMessages.User.userNotFound(username));
        }
        if (!dto.newPassword().equals(dto.repeatedNewPassword())) {
            throw new WrongCredentialsException(ExceptionMessages.Auth.newPasswordsDoNotMatch());
        }
        if (!jwtUtils.isValid(dto.token()) || !tokenRepository.existsByTokenValueAndTokenType(dto.token(), TokenType.FORGOT_PASSWORD)) {
            throw new AlreadyChangedPasswordException(ExceptionMessages.Auth.alreadyChangedPassword());
        }

        user.get().setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user.get());
        tokenRepository.deleteByTokenValueAndTokenType(dto.token(), TokenType.FORGOT_PASSWORD);
    }

    Token createToken(User user, TokenType type, long validity) {
        String generatedToken = jwtUtils.generateToken(user, validity);
        Token token = new Token(generatedToken, type, user);
        return tokenRepository.save(token);
    }
}
