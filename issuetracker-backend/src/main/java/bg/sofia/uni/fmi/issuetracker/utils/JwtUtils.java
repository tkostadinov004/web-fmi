package bg.sofia.uni.fmi.issuetracker.utils;

import bg.sofia.uni.fmi.issuetracker.model.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtils.class);
    private final Environment environment;

    public JwtUtils(Environment environment) {
        this.environment = environment;
    }

    /**
     * Checks if the token is valid.
     * A valid token is a token that has not been tampered and is not expired.
     *
     * @param token the token to be validated
     * @return {@code true if the token is valid}, otherwise {@code false}
     */
    public boolean isValid(String token) {
        Algorithm algorithm = Algorithm.HMAC256(getSignInKey());
        JWTVerifier verifier = JWT.require(algorithm).build();

        try {
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException ex) {
            LOGGER.error(ex.getMessage());
            return false;
        }
    }

    public String extractUsername(String token) {
        return JWT.decode(token).getSubject();
    }

    public String generateToken(User user, long validFor) {
        Algorithm algorithm = Algorithm.HMAC256(getSignInKey());

        return JWT
                .create()
                .withSubject(user.getUsername())
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + validFor))
                .sign(algorithm);
    }

    private byte[] getSignInKey() {
        return Base64.getDecoder().decode(environment.getProperty(Constants.JWT_KEY_ENV_PATH));
    }
}
