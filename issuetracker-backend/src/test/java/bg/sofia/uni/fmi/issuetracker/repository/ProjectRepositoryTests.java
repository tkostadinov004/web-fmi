package bg.sofia.uni.fmi.issuetracker.repository;

import bg.sofia.uni.fmi.issuetracker.model.Project;
import bg.sofia.uni.fmi.issuetracker.model.ProjectUser;
import bg.sofia.uni.fmi.issuetracker.model.User;
import bg.sofia.uni.fmi.issuetracker.model.auth.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ProjectRepositoryTests {
    private static List<User> TEST_USERS;
    private static List<Project> TEST_PROJECTS;
    private static List<ProjectUser> TEST_PROJECT_USERS;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @BeforeEach
    @Transactional
    void seed() {
        TEST_USERS = List.of(
                User.UserBuilder.newBuilder().firstName("FirstName1").lastName("LastName1").username("user1").email("email1@email.com").password("encryptedPass1").build(),
                User.UserBuilder.newBuilder().firstName("FirstName2").lastName("LastName2").username("user2").email("email2@email.com").password("encryptedPass2").build(),
                User.UserBuilder.newBuilder().firstName("FirstName3").lastName("LastName3").username("user3").email("email3@email.com").password("encryptedPass3").build()
        );
        TEST_PROJECTS = List.of(
                new Project("project1"),
                new Project("project2"),
                new Project("project3")
        );
        TEST_PROJECT_USERS = List.of(
                new ProjectUser(TEST_PROJECTS.get(0), TEST_USERS.get(1), Role.TEAM_LEAD),
                new ProjectUser(TEST_PROJECTS.get(1), TEST_USERS.get(1), Role.DEVELOPER)
        );

        TEST_USERS = userRepository.saveAll(TEST_USERS);
        TEST_PROJECTS = projectRepository.saveAll(TEST_PROJECTS);
        TEST_PROJECT_USERS = projectUserRepository.saveAll(TEST_PROJECT_USERS);
    }

    @AfterEach
    @Transactional
    void cleanup() {
        projectUserRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testIsUserInProject() {
        assertFalse(projectRepository.isUserInProject(TEST_USERS.get(2), TEST_PROJECTS.get(0)));
        assertTrue(projectRepository.isUserInProject(TEST_USERS.get(1), TEST_PROJECTS.get(0)));
    }

    @Test
    void testHasRoles() {
        assertTrue(projectRepository.hasRoles(TEST_USERS.get(1), TEST_PROJECTS.get(0), List.of(Role.TEAM_LEAD, Role.DEVELOPER)));
        assertFalse(projectRepository.hasRoles(TEST_USERS.get(2), TEST_PROJECTS.get(2), List.of(Role.TEAM_LEAD)));
    }

    @Test
    void testHasRolesStrict() {
        assertFalse(projectRepository.hasRolesStrict(TEST_USERS.get(1), TEST_PROJECTS.get(0), List.of(Role.TEAM_LEAD, Role.DEVELOPER)));
    }
}
