package bg.sofia.uni.fmi.issuetracker;

import bg.sofia.uni.fmi.issuetracker.model.auth.Token;
import bg.sofia.uni.fmi.issuetracker.model.auth.User;
import bg.sofia.uni.fmi.issuetracker.model.project.Project;

import java.util.Set;

public class TestData {
    public static final User TEST_USER =
        new User("FirstName", "LastName", "user", "encryptedPass");
    public static final Project TEST_PROJECT =
        new Project("testProject", Set.of());
    public static final Token TEST_TOKEN =
        new Token("testToken", TEST_USER);
}
