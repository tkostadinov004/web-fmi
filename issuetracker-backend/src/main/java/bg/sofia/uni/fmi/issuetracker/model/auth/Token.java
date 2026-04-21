package bg.sofia.uni.fmi.issuetracker.model.auth;

import jakarta.persistence.*;

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
}
