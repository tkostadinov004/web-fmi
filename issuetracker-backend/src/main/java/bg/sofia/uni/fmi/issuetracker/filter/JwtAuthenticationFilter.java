package bg.sofia.uni.fmi.issuetracker.filter;

import bg.sofia.uni.fmi.issuetracker.model.auth.TokenType;
import bg.sofia.uni.fmi.issuetracker.repository.TokenRepository;
import bg.sofia.uni.fmi.issuetracker.service.contract.UserService;
import bg.sofia.uni.fmi.issuetracker.utils.JwtUtils;
import bg.sofia.uni.fmi.issuetracker.utils.messages.OutputMessages;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static bg.sofia.uni.fmi.issuetracker.utils.CommonUtils.buildErrorResponseAsJson;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final TokenRepository tokenRepository;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(TokenRepository tokenRepository, UserService userService, JwtUtils jwtUtils) {
        this.tokenRepository = tokenRepository;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/v3/api-docs") ||
                request.getRequestURI().startsWith("/swagger-ui") ||
                request.getRequestURI().startsWith("/auth/login") ||
                request.getRequestURI().startsWith("/auth/register") ||
                request.getRequestURI().startsWith("/auth/forgotPassword") ||
                request.getRequestURI().startsWith("/auth/refresh");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            outputUnauthorized(response, false);
            return;
        }

        String token = authHeader.substring(7);
        if (!jwtUtils.isValid(token) || !tokenRepository.existsByTokenValueAndTokenType(token, TokenType.AUTH)) {
            outputUnauthorized(response, true);
            return;
        }

        String username = jwtUtils.extractUsername(token);
        if (userService.isDeleted(username)) {
            outputUnauthorized(response, false);
            return;
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, null, List.of());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }

    void outputUnauthorized(HttpServletResponse response, boolean isExpired) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(buildErrorResponseAsJson(OutputMessages.System.UNAUTHORIZED, isExpired));
    }
}