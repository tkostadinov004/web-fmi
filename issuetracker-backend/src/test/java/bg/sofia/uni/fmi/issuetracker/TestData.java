package bg.sofia.uni.fmi.issuetracker;

import bg.sofia.uni.fmi.issuetracker.model.Project;
import bg.sofia.uni.fmi.issuetracker.model.User;
import bg.sofia.uni.fmi.issuetracker.model.auth.Token;

import java.util.Set;

public class TestData {
    public static final User TEST_USER = User.UserBuilder.newBuilder()
            .firstName("FirstName")
            .lastName("LastName")
            .username("user")
            .password("encryptedPass")
            .email("email@email.com")
            .build();
    public static final Project TEST_PROJECT =
            new Project("testProject", Set.of());
    public static final Project TEST_PROJECT_2 =
            new Project("testProject2", Set.of());
    public static final Token TEST_TOKEN =
            new Token("testToken", TEST_USER);
}
