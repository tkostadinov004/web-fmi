package bg.sofia.uni.fmi.issuetracker.filter;

import bg.sofia.uni.fmi.issuetracker.repository.TokenRepository;
import bg.sofia.uni.fmi.issuetracker.utils.JwtUtils;
import bg.sofia.uni.fmi.issuetracker.utils.messages.OutputMessages;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
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
import tools.jackson.databind.ObjectMapper;

import java.io.PrintWriter;
import java.util.Map;

import static bg.sofia.uni.fmi.issuetracker.TestConstants.TEST_TOKEN;
import static bg.sofia.uni.fmi.issuetracker.TestConstants.TEST_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    private JwtUtils jwtUtils;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @Mock
    private PrintWriter mockWriter;

    @BeforeEach
    void setUp() throws Exception {
        when(request.getRequestURI()).thenReturn("/logic/test");
    }

    @Test
    void testFilter_DoesNotCheckTokenWithAuthEndpoints() throws Exception {
        when(request.getRequestURI()).thenReturn("/auth/something");

        filter.doFilterInternal(request, response, filterChain);

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
        verifyUnauthorizedResponse();
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void testFilter_StopsExecutionOnNonexistentToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + TEST_TOKEN.getTokenValue());
        when(jwtUtils.isValid(anyString())).thenReturn(true);
        when(tokenRepository.existsByTokenValue(anyString())).thenReturn(false);
        when(response.getWriter()).thenReturn(mockWriter);

        filter.doFilterInternal(request, response, filterChain);
        verifyUnauthorizedResponse();
        verify(filterChain, never()).doFilter(any(), any());
        verify(jwtUtils, times(1)).isValid(TEST_TOKEN.getTokenValue());
        verify(tokenRepository, times(1)).existsByTokenValue(TEST_TOKEN.getTokenValue());
    }

    @Test
    void testFilter_SuccessfullyAuthenticated() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + TEST_TOKEN.getTokenValue());
        when(jwtUtils.extractUsername(anyString())).thenReturn(TEST_USER.getUsername());
        when(jwtUtils.isValid(anyString())).thenReturn(true);
        when(tokenRepository.existsByTokenValue(anyString())).thenReturn(true);
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
        verifyUnauthorizedResponse();
        verify(jwtUtils, never()).extractUsername(anyString());
    }

    private void verifyUnauthorizedResponse() throws Exception {
        verify(request, times(1)).getHeader("Authorization");
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response, times(1)).setContentType("application/json");
        verify(response, times(1)).getWriter();
        verify(mockWriter, times(1)).write(new ObjectMapper().writeValueAsString(Map.of("message", OutputMessages.System.UNAUTHORIZED)));
    }
}
