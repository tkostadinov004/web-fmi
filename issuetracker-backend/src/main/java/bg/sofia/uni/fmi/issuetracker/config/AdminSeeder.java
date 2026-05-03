package bg.sofia.uni.fmi.issuetracker.config;

import bg.sofia.uni.fmi.issuetracker.model.User;
import bg.sofia.uni.fmi.issuetracker.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${services.app.system_admin.first_name}")
    private String adminFirstName;
    @Value("${services.app.system_admin.last_name}")
    private String adminLastName;
    @Value("${services.app.system_admin.username}")
    private String adminUsername;
    @Value("${services.app.system_admin.email}")
    private String adminEmail;
    @Value("${services.app.system_admin.password}")
    private String adminPassword;

    public AdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void seedAdmin() {
        if (userRepository.existsById(adminUsername)) {
            return;
        }

        User admin = User.UserBuilder.newBuilder()
                .firstName(adminFirstName)
                .lastName(adminLastName)
                .username(adminUsername)
                .email(adminEmail)
                .companyName("admin")
                .password(passwordEncoder.encode(adminPassword))
                .admin(true)
                .build();
        userRepository.save(admin);
    }
}
