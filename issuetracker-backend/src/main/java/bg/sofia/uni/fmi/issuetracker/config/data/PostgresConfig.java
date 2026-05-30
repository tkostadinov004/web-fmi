package bg.sofia.uni.fmi.issuetracker.config.data;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class PostgresConfig {
    @Value("${services.db.postgres.connection_url}")
    private String connectionUrl;

    @Value("${services.db.postgres.port}")
    private int port;

    @Value("${services.db.postgres.driver_class_name}")
    private String driverClassName;

    @Value("${services.db.postgres.database_name}")
    private String databaseName;

    @Value("${services.db.postgres.username}")
    private String username;

    @Value("${services.db.postgres.password}")
    private String password;

    @Bean
    public DataSource dataSource() {
        String fullUrl = "%s:%d/%s".formatted(connectionUrl, port, databaseName);

        return DataSourceBuilder
                .create()
                .type(HikariDataSource.class)
                .url(fullUrl)
                .driverClassName(driverClassName)
                .username(username)
                .password(password)
                .build();
    }
}
