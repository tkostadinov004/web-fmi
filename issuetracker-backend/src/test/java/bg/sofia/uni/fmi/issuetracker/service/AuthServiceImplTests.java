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
import bg.sofia.uni.fmi.issuetracker.utils.Constants;
import bg.sofia.uni.fmi.issuetracker.utils.EmailUtils;
import bg.sofia.uni.fmi.issuetracker.utils.JwtUtils;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import bg.sofia.uni.fmi.issuetracker.utils.messages.OutputMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static bg.sofia.uni.fmi.issuetracker.TestData.TEST_TOKEN;
import static bg.sofia.uni.fmi.issuetracker.TestData.TEST_USER;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTests {
    private static final UserRegisterDTO REGISTER_USER =
            new UserRegisterDTO("FirstName", "LastName", "user", "email@email.com", "company", "pass");
    private static final UserLoginDTO LOGIN_USER =
            new UserLoginDTO("user", "pass");

    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailUtils emailUtils;

    @Spy
    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void testRegister_ThrowsWhenUserAlreadyExists() {
        when(userRepository.existsById(REGISTER_USER.username())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(REGISTER_USER))
                .isExactlyInstanceOf(UserAlreadyExistsException.class)
                .hasMessage(ExceptionMessages.User.userAlreadyExists(REGISTER_USER.username()));
    }

    @Test
    void testRegister_Success() {
        when(userRepository.existsById(REGISTER_USER.username())).thenReturn(false);
        doReturn("encodedPass").when(passwordEncoder).encode(any());
        doAnswer(a -> null).when(userRepository).save(any());

        AuthResponse result = authService.register(REGISTER_USER);
        assertEquals(OutputMessages.Auth.SUCCESSFULLY_CREATED_USER, result.message());
        assertNull(result.token());
        verify(passwordEncoder, times(1)).encode(REGISTER_USER.password());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(captor.capture());
        assertEquals(REGISTER_USER.firstName(), captor.getValue().getFirstName());
        assertEquals(REGISTER_USER.lastName(), captor.getValue().getLastName());
        assertEquals(REGISTER_USER.username(), captor.getValue().getUsername());
        assertEquals("encodedPass", captor.getValue().getPassword());
    }

    @Test
    void testLogin_ThrowsOnNonexistentUser() {
        when(userRepository.findById(LOGIN_USER.username())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(LOGIN_USER))
                .hasMessage(ExceptionMessages.Auth.wrongCredentials())
                .isExactlyInstanceOf(WrongCredentialsException.class);
    }

    @Test
    void testLogin_ThrowsOnWrongCredentials() {
        when(userRepository.findById(LOGIN_USER.username())).thenReturn(Optional.of(TEST_USER));
        doReturn(false).when(passwordEncoder).matches(any(), any());

        assertThatThrownBy(() -> authService.login(LOGIN_USER))
                .hasMessage(ExceptionMessages.Auth.wrongCredentials())
                .isExactlyInstanceOf(WrongCredentialsException.class);

        verify(passwordEncoder, times(1)).matches(LOGIN_USER.password(), TEST_USER.getPassword());
    }

    @Test
    void testLogin_ThrowsWhenUserIsAlreadyLoggedIn() {
        when(userRepository.findById(LOGIN_USER.username())).thenReturn(Optional.of(TEST_USER));
        doReturn(true).when(passwordEncoder).matches(any(), any());

        Token token = new Token("testToken", TEST_USER);
        when(tokenRepository.findAllByUserAndTokenType(TEST_USER, TokenType.AUTH)).thenReturn(List.of(token));
        when(jwtUtils.isTokenExpired(token.getTokenValue())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(LOGIN_USER))
                .hasMessage(ExceptionMessages.Auth.userAlreadyLoggedIn(TEST_USER.getUsername()))
                .isExactlyInstanceOf(UserAlreadyLoggedException.class);

        verify(jwtUtils, times(1)).isTokenExpired(token.getTokenValue());
    }

    @Test
    void testLogin_Successfully() {
        when(userRepository.findById(LOGIN_USER.username())).thenReturn(Optional.of(TEST_USER));
        doReturn(true).when(passwordEncoder).matches(any(), any());

        Token oldToken = new Token("oldToken", TEST_USER);
        when(jwtUtils.isTokenExpired(oldToken.getTokenValue())).thenReturn(true);
        when(tokenRepository.findAllByUserAndTokenType(TEST_USER, TokenType.AUTH)).thenReturn(List.of(oldToken));

        Token token = new Token("testToken", TEST_USER);
        doReturn(token).when(authService).createToken(eq(TEST_USER), eq(TokenType.AUTH), any(Long.class));

        AuthResponse response = authService.login(LOGIN_USER);
        assertEquals(OutputMessages.Auth.SUCCESSFULLY_LOGGED_USER, response.message());
        assertEquals(token.getTokenValue(), response.token());

        verify(jwtUtils, times(1)).isTokenExpired(any());
        verify(tokenRepository, times(1)).deleteAll(any());
    }

    @Test
    void testLogout_ThrowsOnNonexistentUser() {
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.logout(TEST_USER.getUsername()))
                .hasMessage(ExceptionMessages.User.userNotFound(TEST_USER.getUsername()))
                .isExactlyInstanceOf(UserNotFoundException.class);
    }

    @Test
    void testLogout_Success() {
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));

        authService.logout(TEST_USER.getUsername());

        verify(tokenRepository, times(1)).deleteAllByUserAndTokenType(TEST_USER, TokenType.AUTH);
    }

    @Test
    public void testChangePassword_ThrowsOnNonexistentOrDeletedUser() {
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.changePassword(LOGIN_USER.username(), null))
                .hasMessage(ExceptionMessages.User.userNotFound(TEST_USER.getUsername()))
                .isExactlyInstanceOf(UserNotFoundException.class);

        User user = new User();
        user.setUsername("user");
        user.setDeleted(true);
        assertThatThrownBy(() -> authService.changePassword(user.getUsername(), null))
                .hasMessage(ExceptionMessages.User.userNotFound(user.getUsername()))
                .isExactlyInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void testChangePassword_ThrowsIfNewPasswordsDoNotMatch() {
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));

        ChangePasswordDTO dto = new ChangePasswordDTO("oldPass", "newPass", "rep");

        assertThatThrownBy(() -> authService.changePassword(LOGIN_USER.username(), dto))
                .hasMessage(ExceptionMessages.Auth.newPasswordsDoNotMatch())
                .isExactlyInstanceOf(WrongCredentialsException.class);
    }

    @Test
    public void testChangePassword_ThrowsIfOldPassIsIncorrect() {
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));

        ChangePasswordDTO dto = new ChangePasswordDTO("oldPass", "newPass", "newPass");
        doReturn(false).when(passwordEncoder).matches(dto.oldPassword(), TEST_USER.getPassword());

        assertThatThrownBy(() -> authService.changePassword(LOGIN_USER.username(), dto))
                .hasMessage(ExceptionMessages.Auth.wrongOldPassword())
                .isExactlyInstanceOf(WrongCredentialsException.class);
    }

    @Test
    public void testChangePassword_Correctly() {
        User user = User.UserBuilder.newBuilder().username("user").password("pass").build();
        when(userRepository.findById(user.getUsername())).thenReturn(Optional.of(user));

        ChangePasswordDTO dto = new ChangePasswordDTO("oldPass", "newPass", "newPass");
        doReturn(true).when(passwordEncoder).matches(dto.oldPassword(), user.getPassword());
        doReturn("encodedPassword").when(passwordEncoder).encode(dto.newPassword());
        doReturn(user).when(userRepository).save(user);

        authService.changePassword(user.getUsername(), dto);
        assertEquals("encodedPassword", user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testSendForgotPasswordEmail_ThrowsOnNonexistentOrDeletedUser() {
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.sendForgotPasswordEmail(LOGIN_USER.username(), null))
                .hasMessage(ExceptionMessages.User.userNotFound(TEST_USER.getUsername()))
                .isExactlyInstanceOf(UserNotFoundException.class);

        User user = new User();
        user.setUsername("user");
        user.setDeleted(true);
        assertThatThrownBy(() -> authService.sendForgotPasswordEmail(user.getUsername(), null))
                .hasMessage(ExceptionMessages.User.userNotFound(user.getUsername()))
                .isExactlyInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void testSendForgotPasswordEmail_ThrowsIfEmailsDoNotMatch() {
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));

        SendForgotPasswordEmailDTO dto = new SendForgotPasswordEmailDTO("nochanceofmatching@email.com", "");

        assertThatThrownBy(() -> authService.sendForgotPasswordEmail(TEST_USER.getUsername(), dto))
                .hasMessage(ExceptionMessages.Auth.wrongEmail())
                .isExactlyInstanceOf(AuthException.class);
    }

    @Test
    public void testSendForgotPasswordEmail_ThrowsIfThereIsCurrentlyAnActiveForgotPasswordSession() {
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));
        SendForgotPasswordEmailDTO dto = new SendForgotPasswordEmailDTO(TEST_USER.getEmail(), "");

        when(tokenRepository.findAllByUserAndTokenType(TEST_USER, TokenType.FORGOT_PASSWORD))
                .thenReturn(List.of(TEST_TOKEN));
        when(jwtUtils.isValid(anyString())).thenReturn(true);

        assertThatThrownBy(() -> authService.sendForgotPasswordEmail(TEST_USER.getUsername(), dto))
                .hasMessage(ExceptionMessages.Auth.forgotPasswordTokenAlreadyExists())
                .isExactlyInstanceOf(ForgotPasswordTokenAlreadyExistsException.class);
    }

    @Test
    public void testSendForgotPasswordEmail_Successfully() {
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));
        when(tokenRepository.findAllByUserAndTokenType(TEST_USER, TokenType.FORGOT_PASSWORD))
                .thenReturn(List.of(TEST_TOKEN));
        when(jwtUtils.isValid(anyString())).thenReturn(false);

        Token token = new Token("testToken", TEST_USER);
        doReturn(token).when(authService).createToken(eq(TEST_USER), eq(TokenType.FORGOT_PASSWORD), any(Long.class));

        SendForgotPasswordEmailDTO dto = new SendForgotPasswordEmailDTO(TEST_USER.getEmail(), "");
        authService.sendForgotPasswordEmail(TEST_USER.getUsername(), dto);

        verify(authService, times(1)).createToken(TEST_USER, TokenType.FORGOT_PASSWORD, Constants.DEFAULT_FORGOT_PASSWORD_TOKEN_VALIDITY);
        verify(emailUtils, times(1)).sendForgotPasswordEmail(dto.email(), dto.redirectUrl(), token);
    }

    @Test
    public void testChangeForgottenPassword_ThrowsOnNonexistentOrDeletedUser() {
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.changeForgottenPassword(LOGIN_USER.username(), null))
                .hasMessage(ExceptionMessages.User.userNotFound(TEST_USER.getUsername()))
                .isExactlyInstanceOf(UserNotFoundException.class);

        User user = new User();
        user.setUsername("user");
        user.setDeleted(true);
        assertThatThrownBy(() -> authService.changeForgottenPassword(user.getUsername(), null))
                .hasMessage(ExceptionMessages.User.userNotFound(user.getUsername()))
                .isExactlyInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void testChangeForgottenPassword_ThrowsIfNewPasswordsDoNotMatch() {
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));

        ChangeForgottenPasswordDTO dto = new ChangeForgottenPasswordDTO("oldPass", "newPass", TEST_TOKEN.getTokenValue());

        assertThatThrownBy(() -> authService.changeForgottenPassword(LOGIN_USER.username(), dto))
                .hasMessage(ExceptionMessages.Auth.newPasswordsDoNotMatch())
                .isExactlyInstanceOf(WrongCredentialsException.class);
    }

    @Test
    public void testChangeForgottenPassword_ThrowsIfPasswordWasAlreadyChanged() {
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));
        ChangeForgottenPasswordDTO dto = new ChangeForgottenPasswordDTO("newPass", "newPass", TEST_TOKEN.getTokenValue());

        doReturn(false).when(jwtUtils).isValid(TEST_TOKEN.getTokenValue());

        assertThatThrownBy(() -> authService.changeForgottenPassword(LOGIN_USER.username(), dto))
                .hasMessage(ExceptionMessages.Auth.alreadyChangedPassword())
                .isExactlyInstanceOf(AlreadyChangedPasswordException.class);

        doReturn(true).when(jwtUtils).isValid(TEST_TOKEN.getTokenValue());
        doReturn(false).when(tokenRepository).existsByTokenValueAndTokenType(TEST_TOKEN.getTokenValue(), TokenType.FORGOT_PASSWORD);

        assertThatThrownBy(() -> authService.changeForgottenPassword(LOGIN_USER.username(), dto))
                .hasMessage(ExceptionMessages.Auth.alreadyChangedPassword())
                .isExactlyInstanceOf(AlreadyChangedPasswordException.class);
    }

    @Test
    public void testChangeForgottenPassword_Correctly() {
        User user = User.UserBuilder.newBuilder().username("user").password("pass").build();
        when(userRepository.findById(user.getUsername())).thenReturn(Optional.of(user));

        ChangeForgottenPasswordDTO dto = new ChangeForgottenPasswordDTO("newPass", "newPass", TEST_TOKEN.getTokenValue());
        doReturn(true).when(jwtUtils).isValid(TEST_TOKEN.getTokenValue());
        doReturn(true).when(tokenRepository).existsByTokenValueAndTokenType(TEST_TOKEN.getTokenValue(), TokenType.FORGOT_PASSWORD);
        doReturn("encodedPassword").when(passwordEncoder).encode(dto.newPassword());
        doReturn(user).when(userRepository).save(user);

        authService.changeForgottenPassword(user.getUsername(), dto);
        assertEquals("encodedPassword", user.getPassword());
        verify(userRepository, times(1)).save(user);
        verify(tokenRepository, times(1)).deleteByTokenValueAndTokenType(TEST_TOKEN.getTokenValue(), TokenType.FORGOT_PASSWORD);
    }


    @Test
    void testCreateToken_Success() {
        Token token = new Token("testToken", TEST_USER);
        when(jwtUtils.generateToken(TEST_USER, Constants.DEFAULT_AUTH_TOKEN_VALIDITY)).thenReturn(token.getTokenValue());
        when(tokenRepository.save(token)).thenReturn(token);

        Token actual = authService.createToken(TEST_USER, TokenType.AUTH, Constants.DEFAULT_AUTH_TOKEN_VALIDITY);
        assertEquals(token, actual);

        ArgumentCaptor<Token> tokenCaptor = ArgumentCaptor.forClass(Token.class);
        verify(tokenRepository, times(1)).save(tokenCaptor.capture());
        assertEquals("testToken", tokenCaptor.getValue().getTokenValue());
        assertEquals(TEST_USER, tokenCaptor.getValue().getUser());
    }
}
