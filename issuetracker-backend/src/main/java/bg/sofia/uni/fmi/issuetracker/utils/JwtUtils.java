package bg.sofia.uni.fmi.issuetracker.utils;

import bg.sofia.uni.fmi.issuetracker.model.auth.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
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

    public boolean isTokenExpired(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getExpiresAt().before(new Date());
    }

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

    public String generateToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256(getSignInKey());

        return JWT
                .create()
                .withSubject(user.getUsername())
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                .sign(algorithm);
    }

    private byte[] getSignInKey() {
        return Base64.getDecoder().decode(environment.getProperty(Constants.JWT_KEY_ENV_PATH));
    }
}
