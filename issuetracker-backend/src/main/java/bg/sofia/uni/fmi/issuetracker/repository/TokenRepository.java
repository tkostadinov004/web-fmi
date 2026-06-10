package bg.sofia.uni.fmi.issuetracker.repository;

import bg.sofia.uni.fmi.issuetracker.model.User;
import bg.sofia.uni.fmi.issuetracker.model.auth.Token;
import bg.sofia.uni.fmi.issuetracker.model.auth.TokenType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {
    boolean existsByTokenValueAndTokenType(String tokenValue, TokenType tokenType);

    void deleteByTokenValueAndTokenType(String tokenValue, TokenType tokenType);

    List<Token> findAllByUserAndTokenType(User user, TokenType tokenType);
    
    boolean existsByTokenValueAndUserAndTokenType(String tokenValue, User user, TokenType tokenType);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    void deleteAllByUserAndTokenTypeIn(User user, Collection<TokenType> tokenTypes);
}
