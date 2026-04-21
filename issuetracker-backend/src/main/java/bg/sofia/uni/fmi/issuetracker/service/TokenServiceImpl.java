package bg.sofia.uni.fmi.issuetracker.service;

import bg.sofia.uni.fmi.issuetracker.model.auth.Token;
import bg.sofia.uni.fmi.issuetracker.model.auth.User;
import bg.sofia.uni.fmi.issuetracker.repository.TokenRepository;
import bg.sofia.uni.fmi.issuetracker.service.contract.TokenService;
import bg.sofia.uni.fmi.issuetracker.utils.JwtUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;
    private final JwtUtils jwtUtils;

    public TokenServiceImpl(TokenRepository tokenRepository, JwtUtils jwtUtils) {
        this.tokenRepository = tokenRepository;
        this.jwtUtils = jwtUtils;
    }

    public boolean isValid(String token, User user) {
        Optional<Token> foundToken = tokenRepository.findTokenByTokenValue(token);
        if (foundToken.isEmpty()) {
            return false;
        }

        String username = jwtUtils.extractUsername(token);
        return (username.equals(user.getUsername())) && !jwtUtils.isTokenExpired(token);
    }
}
