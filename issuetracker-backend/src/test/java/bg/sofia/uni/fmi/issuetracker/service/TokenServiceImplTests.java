package bg.sofia.uni.fmi.issuetracker.service;

import bg.sofia.uni.fmi.issuetracker.exception.user.UserNotFoundException;
import bg.sofia.uni.fmi.issuetracker.model.auth.User;
import bg.sofia.uni.fmi.issuetracker.repository.TokenRepository;
import bg.sofia.uni.fmi.issuetracker.repository.UserRepository;
import bg.sofia.uni.fmi.issuetracker.utils.JwtUtils;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static bg.sofia.uni.fmi.issuetracker.TestConstants.TEST_TOKEN;
import static bg.sofia.uni.fmi.issuetracker.TestConstants.TEST_USER;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TokenServiceImplTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private JwtUtils jwtUtils;

    @Spy
    @InjectMocks
    private TokenServiceImpl tokenService;

    @Test
    void testIsValid_ReturnsFalseIfTokenIsNotFound() {
        String token = "testToken";
        when(tokenRepository.findTokenByTokenValue(token)).thenReturn(Optional.empty());

        assertFalse(tokenService.isValid(token, TEST_USER));
        verify(jwtUtils, never()).extractUsername(anyString());
        verify(jwtUtils, never()).isTokenExpired(anyString());
    }

    @Test
    void testIsValid_ReturnsFalseIfUsernamesDoNotMatch() {
        when(tokenRepository.findTokenByTokenValue(TEST_TOKEN.getTokenValue())).thenReturn(Optional.of(TEST_TOKEN));
        when(jwtUtils.extractUsername(TEST_TOKEN.getTokenValue())).thenReturn("something else");

        assertFalse(tokenService.isValid(TEST_TOKEN.getTokenValue(), TEST_USER));
        verify(jwtUtils, times(1)).extractUsername(anyString());
        verify(jwtUtils, never()).isTokenExpired(anyString());
    }

    @Test
    void testIsValid_ReturnsFalseIfTokenIsExpired() {
        when(tokenRepository.findTokenByTokenValue(TEST_TOKEN.getTokenValue())).thenReturn(Optional.of(TEST_TOKEN));
        when(jwtUtils.extractUsername(TEST_TOKEN.getTokenValue())).thenReturn(TEST_TOKEN.getUser().getUsername());
        when(jwtUtils.isTokenExpired(TEST_TOKEN.getTokenValue())).thenReturn(true);

        assertFalse(tokenService.isValid(TEST_TOKEN.getTokenValue(), TEST_USER));
        verify(jwtUtils, times(1)).extractUsername(anyString());
        verify(jwtUtils, times(1)).isTokenExpired(anyString());
    }

    @Test
    void testIsValid_ReturnsTrueIfTokenIsNotExpiredAndUsersMatch() {
        when(tokenRepository.findTokenByTokenValue(TEST_TOKEN.getTokenValue())).thenReturn(Optional.of(TEST_TOKEN));
        when(jwtUtils.extractUsername(TEST_TOKEN.getTokenValue())).thenReturn(TEST_TOKEN.getUser().getUsername());
        when(jwtUtils.isTokenExpired(TEST_TOKEN.getTokenValue())).thenReturn(false);

        assertTrue(tokenService.isValid(TEST_TOKEN.getTokenValue(), TEST_USER));
        verify(jwtUtils, times(1)).extractUsername(anyString());
        verify(jwtUtils, times(1)).isTokenExpired(anyString());
    }

    @Test
    void testIsValidWithUser_ThrowsOnNonexistentUser() {
        String username = "testUsername";
        when(userRepository.findById(username)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tokenService.isValid("token", username))
                .hasMessage(ExceptionMessages.User.userNotFound(username))
                .isExactlyInstanceOf(UserNotFoundException.class);
    }

    @Test
    void testIsValidWithUser_ExecutesCorrectly() {
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));
        doReturn(true).when(tokenService).isValid(anyString(), any(User.class));

        assertTrue(tokenService.isValid(TEST_TOKEN.getTokenValue(), TEST_USER.getUsername()));
        verify(tokenService, times(1)).isValid(TEST_TOKEN.getTokenValue(), TEST_USER);
    }
}
