package bg.sofia.uni.fmi.issuetracker.service;

import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectDoesNotExistException;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserNotFoundException;
import bg.sofia.uni.fmi.issuetracker.model.auth.Role;
import bg.sofia.uni.fmi.issuetracker.repository.ProjectRepository;
import bg.sofia.uni.fmi.issuetracker.repository.UserRepository;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static bg.sofia.uni.fmi.issuetracker.TestConstants.TEST_PROJECT;
import static bg.sofia.uni.fmi.issuetracker.TestConstants.TEST_USER;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceImplTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Test
    void testIsMemberOf_ThrowsOnNonexistentUser() {
        String username = "testUsername";
        when(userRepository.findById(username)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.isMemberOf(username, "proj"))
                .hasMessage(ExceptionMessages.User.userNotFound(username))
                .isExactlyInstanceOf(UserNotFoundException.class);
    }

    @Test
    void testIsMemberOf_ThrowsOnNonexistentProject() {
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));
        String projectId = "proj";
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.isMemberOf(TEST_USER.getUsername(), projectId))
                .hasMessage(ExceptionMessages.Project.projectNotFound(projectId))
                .isExactlyInstanceOf(ProjectDoesNotExistException.class);
    }

    @Test
    void testIsMemberOf_ReturnsTrueWhenUserIsInProject() {
        isMemberOf_SuccessScenario(true);
    }

    @Test
    void testIsMemberOf_ReturnsFalseWhenUserIsNotInProject() {
        isMemberOf_SuccessScenario(false);
    }

    @Test
    void testHasRoles_ThrowsOnNonexistentUser() {
        String username = "testUsername";
        when(userRepository.findById(username)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.hasRoles(username, "proj", List.of(), false))
                .hasMessage(ExceptionMessages.User.userNotFound(username))
                .isExactlyInstanceOf(UserNotFoundException.class);
    }

    @Test
    void testHasRoles_ThrowsOnNonexistentProject() {
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));
        String projectId = "proj";
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.hasRoles(TEST_USER.getUsername(), projectId, List.of(), false))
                .hasMessage(ExceptionMessages.Project.projectNotFound(projectId))
                .isExactlyInstanceOf(ProjectDoesNotExistException.class);
    }

    @Test
    void testHasRoles_ReturnsStrictCheckResult() {
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));
        when(projectRepository.findById(TEST_PROJECT.getUuid())).thenReturn(Optional.of(TEST_PROJECT));
        when(projectRepository.hasRolesStrict(any(), any(), any())).thenReturn(true);

        List<Role> roles = List.of(Role.ADMIN);
        assertTrue(projectService.hasRoles(TEST_USER.getUsername(), TEST_PROJECT.getUuid(), roles, true));
        verify(projectRepository, times(1)).hasRolesStrict(TEST_USER, TEST_PROJECT, roles);
        verify(projectRepository, never()).hasRoles(TEST_USER, TEST_PROJECT, roles);
    }

    @Test
    void testHasRoles_ReturnsNonStrictCheckResult() {
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));
        when(projectRepository.findById(TEST_PROJECT.getUuid())).thenReturn(Optional.of(TEST_PROJECT));
        when(projectRepository.hasRoles(any(), any(), any())).thenReturn(true);

        List<Role> roles = List.of(Role.ADMIN);
        assertTrue(projectService.hasRoles(TEST_USER.getUsername(), TEST_PROJECT.getUuid(), roles, false));
        verify(projectRepository, times(1)).hasRoles(TEST_USER, TEST_PROJECT, roles);
        verify(projectRepository, never()).hasRolesStrict(TEST_USER, TEST_PROJECT, roles);
    }

    private void isMemberOf_SuccessScenario(boolean isUserInProject) {
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));
        when(projectRepository.findById(TEST_PROJECT.getUuid())).thenReturn(Optional.of(TEST_PROJECT));
        when(projectRepository.isUserInProject(any(), any())).thenReturn(isUserInProject);

        assertEquals(isUserInProject, projectService.isMemberOf(TEST_USER.getUsername(), TEST_PROJECT.getUuid()));
        verify(projectRepository, times(1)).isUserInProject(TEST_USER, TEST_PROJECT);
    }
}
