package bg.sofia.uni.fmi.issuetracker.filter;

import bg.sofia.uni.fmi.issuetracker.model.auth.User;
import bg.sofia.uni.fmi.issuetracker.repository.UserRepository;
import bg.sofia.uni.fmi.issuetracker.service.contract.TokenService;
import bg.sofia.uni.fmi.issuetracker.utils.JwtUtils;
import bg.sofia.uni.fmi.issuetracker.utils.messages.OutputMessages;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static bg.sofia.uni.fmi.issuetracker.utils.CommonUtils.buildErrorResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            outputUnauthorized(response);
            return;
        }

        String token = authHeader.substring(7);
        String username = jwtUtils.extractUsername(token);
        Optional<User> user = userRepository.findById(username);

        if (user.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (tokenService.isValid(token, user.get())) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user.get(), null, List.of()
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
                filterChain.doFilter(request, response);
                return;
            }
        }
        outputUnauthorized(response);
    }

    void outputUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(buildErrorResponse(OutputMessages.System.UNAUTHORIZED));
    }
}