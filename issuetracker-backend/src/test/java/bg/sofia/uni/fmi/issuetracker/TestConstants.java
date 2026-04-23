package bg.sofia.uni.fmi.issuetracker;

import bg.sofia.uni.fmi.issuetracker.model.auth.Project;
import bg.sofia.uni.fmi.issuetracker.model.auth.User;

import java.util.Set;

public class TestConstants {
    public static final User TEST_USER =
            new User("FirstName", "LastName", "user", "encryptedPass");
    public static final Project TEST_PROJECT =
            new Project("testProject", Set.of());
}
