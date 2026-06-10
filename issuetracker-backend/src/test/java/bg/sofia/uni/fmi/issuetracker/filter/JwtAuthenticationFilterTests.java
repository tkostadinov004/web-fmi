package bg.sofia.uni.fmi.issuetracker.filter;

import bg.sofia.uni.fmi.issuetracker.model.auth.TokenType;
import bg.sofia.uni.fmi.issuetracker.repository.TokenRepository;
import bg.sofia.uni.fmi.issuetracker.service.UserServiceImpl;
import bg.sofia.uni.fmi.issuetracker.utils.JwtUtils;
import bg.sofia.uni.fmi.issuetracker.utils.messages.OutputMessages;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.PrintWriter;

import static bg.sofia.uni.fmi.issuetracker.TestData.TEST_TOKEN;
import static bg.sofia.uni.fmi.issuetracker.TestData.TEST_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTests {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @Mock
    private PrintWriter mockWriter;

    @Test
    void testFilter_DoesNotCheckTokenWithAuthEndpoints() throws Exception {
        when(request.getRequestURI()).thenReturn("/auth/login");

        filter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(request, never()).getHeader("Authorization");
    }

    @Test
    void testFilter_StopsExecutionOnNullAuthHeader() throws Exception {
        invalidAuthHeaderTest(null);
    }

    @Test
    void testFilter_StopsExecutionOnInvalidAuthHeaderFormat() throws Exception {
        invalidAuthHeaderTest("something else");
    }

    @Test
    void testFilter_StopsExecutionOnInvalidToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + TEST_TOKEN.getTokenValue());
        when(jwtUtils.isValid(anyString())).thenReturn(false);
        when(response.getWriter()).thenReturn(mockWriter);

        filter.doFilterInternal(request, response, filterChain);
        verifyUnauthorizedResponse(true);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void testFilter_StopsExecutionOnNonexistentToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + TEST_TOKEN.getTokenValue());
        when(jwtUtils.isValid(anyString())).thenReturn(true);
        when(tokenRepository.existsByTokenValueAndTokenType(anyString(), eq(TokenType.AUTH))).thenReturn(false);
        when(response.getWriter()).thenReturn(mockWriter);

        filter.doFilterInternal(request, response, filterChain);
        verifyUnauthorizedResponse(true);
        verify(filterChain, never()).doFilter(any(), any());
        verify(jwtUtils, times(1)).isValid(TEST_TOKEN.getTokenValue());
        verify(tokenRepository, times(1)).existsByTokenValueAndTokenType(TEST_TOKEN.getTokenValue(), TokenType.AUTH);
    }

    @Test
    void testFilter_StopsExecutionOnDeletedUser() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + TEST_TOKEN.getTokenValue());
        when(jwtUtils.extractUsername(anyString())).thenReturn(TEST_USER.getUsername());
        when(jwtUtils.isValid(anyString())).thenReturn(true);
        when(tokenRepository.existsByTokenValueAndTokenType(anyString(), eq(TokenType.AUTH))).thenReturn(true);
        when(userService.isDeleted(TEST_USER.getUsername())).thenReturn(true);
        when(response.getWriter()).thenReturn(mockWriter);

        filter.doFilterInternal(request, response, filterChain);
        verifyUnauthorizedResponse(false);
        verify(filterChain, never()).doFilter(any(), any());
        verify(jwtUtils, times(1)).isValid(TEST_TOKEN.getTokenValue());
        verify(tokenRepository, times(1)).existsByTokenValueAndTokenType(TEST_TOKEN.getTokenValue(), TokenType.AUTH);
        verify(userService, times(1)).isDeleted(TEST_USER.getUsername());
    }

    @Test
    void testFilter_SuccessfullyAuthenticated() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + TEST_TOKEN.getTokenValue());
        when(jwtUtils.extractUsername(anyString())).thenReturn(TEST_USER.getUsername());
        when(jwtUtils.isValid(anyString())).thenReturn(true);
        when(tokenRepository.existsByTokenValueAndTokenType(anyString(), eq(TokenType.AUTH))).thenReturn(true);
        when(userService.isDeleted(TEST_USER.getUsername())).thenReturn(false);
        SecurityContext mockSecurityContext = mock();

        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);

            filter.doFilterInternal(request, response, filterChain);
        }

        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(mockSecurityContext, times(1)).setAuthentication(captor.capture());
        UsernamePasswordAuthenticationToken token = captor.getValue();
        assertInstanceOf(String.class, token.getPrincipal());
        assertEquals(TEST_USER.getUsername(), token.getPrincipal());

        verify(filterChain, times(1)).doFilter(request, response);
    }

    private void invalidAuthHeaderTest(String authHeader) throws Exception {
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(response.getWriter()).thenReturn(mockWriter);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);
        verifyUnauthorizedResponse(false);
        verify(jwtUtils, never()).extractUsername(anyString());
    }

    private void verifyUnauthorizedResponse(boolean isExpired) throws Exception {
        verify(request, times(1)).getHeader("Authorization");
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response, times(1)).setContentType("application/json");
        verify(response, times(1)).getWriter();
        verify(mockWriter, times(1)).write(JwtAuthenticationFilter.buildErrorResponse(OutputMessages.System.UNAUTHORIZED, isExpired));
    }
}
