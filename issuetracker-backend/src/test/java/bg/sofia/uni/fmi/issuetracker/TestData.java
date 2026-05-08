package bg.sofia.uni.fmi.issuetracker;

import bg.sofia.uni.fmi.issuetracker.model.FeatureFlag;
import bg.sofia.uni.fmi.issuetracker.model.User;
import bg.sofia.uni.fmi.issuetracker.model.auth.Token;
import bg.sofia.uni.fmi.issuetracker.model.project.Project;

import java.util.List;
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
            new Project("testProject", Set.of(), List.of());
    public static final Project TEST_PROJECT_2 =
            new Project("testProject2", Set.of(), List.of());
    public static final Token TEST_TOKEN =
            new Token("testToken", TEST_USER);
    public static final FeatureFlag TEST_FEATURE_FLAG_1 = new FeatureFlag("FF_1", true);
    public static final FeatureFlag TEST_FEATURE_FLAG_2 = new FeatureFlag("FF_2", false);
    public static final List<FeatureFlag> FEATURE_FLAGS = List.of(TEST_FEATURE_FLAG_1, TEST_FEATURE_FLAG_2);
}
