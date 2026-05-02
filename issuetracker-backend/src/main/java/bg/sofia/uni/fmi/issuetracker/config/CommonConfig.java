package bg.sofia.uni.fmi.issuetracker.config;

import bg.sofia.uni.fmi.issuetracker.utils.FileServiceRoot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class CommonConfig {
    @Bean
    public FileServiceRoot fileServiceRoot() {
        Path path = Paths.get("src/main/resources/files");
        return new FileServiceRoot(path);
    }
}
