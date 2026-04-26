package bg.sofia.uni.fmi.issuetracker.model.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "tokens")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid")
    private String uuid;

    @Column(name = "token_value", length = 1024)
    private String tokenValue;

    @ManyToOne
    @JoinColumn(name = "user_username")
    private User user;

    public Token() {
    }

    public Token(String tokenValue, User user) {
        this.tokenValue = tokenValue;
        this.user = user;
    }

    public String getUuid() {
        return uuid;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public User getUser() {
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return Objects.equals(uuid, token.uuid) && Objects.equals(tokenValue, token.tokenValue) && Objects.equals(user, token.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, tokenValue, user);
    }
}
