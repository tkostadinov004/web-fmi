package bg.sofia.uni.fmi.issuetracker.config.data;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.SessionConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Neo4jConfig {
    @Value("${services.db.neo.connection_url}")
    private String connectionUrl;

    @Value("${services.db.neo.port}")
    private int port;

    @Value("${services.db.neo.database_name}")
    private String databaseName;

    @Value("${services.db.neo.username}")
    private String username;

    @Value("${services.db.neo.password}")
    private String password;

    @Bean
    public Driver driver() {
        return GraphDatabase.driver(connectionUrl + ":" + port, AuthTokens.basic(username, password));
    }

    @Bean
    public SessionConfig sessionConfig() {
        return SessionConfig.forDatabase(databaseName);
    }
}