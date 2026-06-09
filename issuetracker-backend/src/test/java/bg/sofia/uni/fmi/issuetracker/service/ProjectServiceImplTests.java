package bg.sofia.uni.fmi.issuetracker.service;

import bg.sofia.uni.fmi.issuetracker.dto.input.project.workflow.ProjectWorkflowDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.project.workflow.WorkflowTransitionDTO;
import bg.sofia.uni.fmi.issuetracker.exception.project.InvalidWorkflowException;
import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectNotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectUserAlreadyInProjectException;
import bg.sofia.uni.fmi.issuetracker.exception.project.UnauthorizedProjectModificationException;
import bg.sofia.uni.fmi.issuetracker.exception.project.UserNotPartOfProjectException;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserNotFoundException;
import bg.sofia.uni.fmi.issuetracker.model.auth.Role;
import bg.sofia.uni.fmi.issuetracker.model.project.Project;
import bg.sofia.uni.fmi.issuetracker.model.project.ProjectUser;
import bg.sofia.uni.fmi.issuetracker.repository.ProjectRepository;
import bg.sofia.uni.fmi.issuetracker.repository.ProjectUserRepository;
import bg.sofia.uni.fmi.issuetracker.repository.TicketRepository;
import bg.sofia.uni.fmi.issuetracker.repository.UserRepository;
import bg.sofia.uni.fmi.issuetracker.repository.neo.NeoProjectRepository;
import bg.sofia.uni.fmi.issuetracker.repository.neo.WorkflowRepository;
import bg.sofia.uni.fmi.issuetracker.service.mapper.ProjectMapper;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static bg.sofia.uni.fmi.issuetracker.TestData.CREATE_PROJECT_DTO;
import static bg.sofia.uni.fmi.issuetracker.TestData.CREATE_PROJECT_USER_DTO;
import static bg.sofia.uni.fmi.issuetracker.TestData.PROJECT_USER;
import static bg.sofia.uni.fmi.issuetracker.TestData.PROJECT_USER_2;
import static bg.sofia.uni.fmi.issuetracker.TestData.TEST_PROJECT;
import static bg.sofia.uni.fmi.issuetracker.TestData.TEST_PROJECT_2;
import static bg.sofia.uni.fmi.issuetracker.TestData.TEST_USER;
import static bg.sofia.uni.fmi.issuetracker.TestData.TEST_USER_2;
import static bg.sofia.uni.fmi.issuetracker.TestData.UPDATE_PROJECT_DTO;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
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
    @Mock
    private ProjectUserRepository projectUserRepository;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private NeoProjectRepository neoProjectRepository;
    @Mock
    private WorkflowRepository workflowRepository;
    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectServiceImpl projectService;

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
                .isExactlyInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    void testHasRoles_ReturnsStrictCheckResult() {
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));
        when(projectRepository.findById(TEST_PROJECT.getUuid())).thenReturn(Optional.of(TEST_PROJECT));
        when(projectRepository.hasRolesStrict(any(), any(), any())).thenReturn(true);

        List<Role> roles = List.of(Role.TEAM_LEAD);
        assertTrue(projectService.hasRoles(TEST_USER.getUsername(), TEST_PROJECT.getUuid(), roles, true));
        verify(projectRepository, times(1)).hasRolesStrict(TEST_USER, TEST_PROJECT, roles);
        verify(projectRepository, never()).hasRoles(TEST_USER, TEST_PROJECT, roles);
    }

    @Test
    void testHasRoles_ReturnsNonStrictCheckResult() {
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));
        when(projectRepository.findById(TEST_PROJECT.getUuid())).thenReturn(Optional.of(TEST_PROJECT));
        when(projectRepository.hasRoles(any(), any(), any())).thenReturn(true);

        List<Role> roles = List.of(Role.TEAM_LEAD);
        assertTrue(projectService.hasRoles(TEST_USER.getUsername(), TEST_PROJECT.getUuid(), roles, false));
        verify(projectRepository, times(1)).hasRoles(TEST_USER, TEST_PROJECT, roles);
        verify(projectRepository, never()).hasRolesStrict(TEST_USER, TEST_PROJECT, roles);
    }

    @Test
    void testGetProjectUsers_ThrowsOnNonexistentProject() {
        String projectId = "non-existent";
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.getProjectUsers(projectId))
                .hasMessage(ExceptionMessages.Project.projectNotFound(projectId))
                .isExactlyInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    void testGetProjectUsers_ReturnsProjectUsersSuccessfully() {
        when(projectRepository.findById(TEST_PROJECT.getUuid())).thenReturn(Optional.of(TEST_PROJECT));
        when(projectUserRepository.findAllByProject(TEST_PROJECT))
                .thenReturn(List.of(PROJECT_USER, PROJECT_USER_2));

        var result = projectService.getProjectUsers(TEST_PROJECT.getUuid());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(dto -> dto.username().equals(TEST_USER.getUsername())));
        assertTrue(result.stream().anyMatch(dto -> dto.username().equals(TEST_USER_2.getUsername())));
    }

    @Test
    void testAddProjectUser_ThrowsOnNonexistentProject() {
        String projectId = "non-existent";
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.addProjectUser(projectId, CREATE_PROJECT_USER_DTO, TEST_USER.getUsername()))
                .hasMessage(ExceptionMessages.Project.projectNotFound(projectId))
                .isExactlyInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    void testAddProjectUser_ThrowsOnNonexistentUser() {
        when(projectRepository.findById(TEST_PROJECT.getUuid())).thenReturn(Optional.of(TEST_PROJECT));
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));
        when(projectRepository.isUserInProject(TEST_USER, TEST_PROJECT, Role.TEAM_LEAD)).thenReturn(true);
        when(userRepository.findById(CREATE_PROJECT_USER_DTO.username())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.addProjectUser(TEST_PROJECT.getUuid(), CREATE_PROJECT_USER_DTO, TEST_USER.getUsername()))
                .hasMessage(ExceptionMessages.User.userNotFound(CREATE_PROJECT_USER_DTO.username()))
                .isExactlyInstanceOf(UserNotFoundException.class);
    }

    @Test
    void testAddProjectUser_ThrowsOnAddingInitiatorIsNotTeamLead() {
        when(projectRepository.findById(TEST_PROJECT.getUuid())).thenReturn(Optional.of(TEST_PROJECT));
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));
        when(projectRepository.isUserInProject(TEST_USER, TEST_PROJECT, Role.TEAM_LEAD)).thenReturn(false);

        assertThatThrownBy(() -> projectService.addProjectUser(TEST_PROJECT.getUuid(), CREATE_PROJECT_USER_DTO, TEST_USER.getUsername()))
                .hasMessage(ExceptionMessages.ProjectUser.cannotAddUserToProject(TEST_PROJECT.getUuid(), TEST_USER.getUsername()))
                .isExactlyInstanceOf(UnauthorizedProjectModificationException.class);
    }

    @Test
    void testAddProjectUser_ThrowsWhenUserAlreadyInProject() {
        when(projectRepository.findById(TEST_PROJECT.getUuid())).thenReturn(Optional.of(TEST_PROJECT));
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));
        when(projectRepository.isUserInProject(TEST_USER, TEST_PROJECT, Role.TEAM_LEAD)).thenReturn(true);
        when(userRepository.findById(TEST_USER_2.getUsername())).thenReturn(Optional.of(TEST_USER_2));
        when(projectRepository.isUserInProject(TEST_USER_2, TEST_PROJECT, CREATE_PROJECT_USER_DTO.role())).thenReturn(true);

        assertThatThrownBy(() -> projectService.addProjectUser(TEST_PROJECT.getUuid(), CREATE_PROJECT_USER_DTO, TEST_USER.getUsername()))
                .hasMessage(ExceptionMessages.ProjectUser.userAlreadyInProject(TEST_USER_2.getUsername(), TEST_PROJECT.getUuid(), CREATE_PROJECT_USER_DTO.role()))
                .isExactlyInstanceOf(ProjectUserAlreadyInProjectException.class);
    }

    @Test
    void testAddProjectUser_SuccessfullyAddsUserToProject() {
        when(projectRepository.findById(TEST_PROJECT.getUuid())).thenReturn(Optional.of(TEST_PROJECT));
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));
        when(projectRepository.isUserInProject(TEST_USER, TEST_PROJECT, Role.TEAM_LEAD)).thenReturn(true);
        when(userRepository.findById(TEST_USER_2.getUsername())).thenReturn(Optional.of(TEST_USER_2));
        when(projectRepository.isUserInProject(TEST_USER_2, TEST_PROJECT, CREATE_PROJECT_USER_DTO.role())).thenReturn(false);

        doReturn(new ProjectUser(TEST_PROJECT, TEST_USER_2, CREATE_PROJECT_USER_DTO.role())).when(projectUserRepository).save(any());
        var result = projectService.addProjectUser(TEST_PROJECT.getUuid(), CREATE_PROJECT_USER_DTO, TEST_USER.getUsername());

        assertNotNull(result);
        assertEquals(TEST_USER_2.getUsername(), result.username());
        verify(projectUserRepository, times(1)).save(any(ProjectUser.class));
    }

    @Test
    void testDeleteProjectUser_ThrowsOnNonexistentProject() {
        String projectId = "non-existent";
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.deleteProjectUser(projectId, TEST_USER.getUsername(), TEST_USER_2.getUsername()))
                .hasMessage(ExceptionMessages.Project.projectNotFound(projectId))
                .isExactlyInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    void testDeleteProjectUser_ThrowsOnNonexistentInitiator() {
        when(projectRepository.findById(TEST_PROJECT.getUuid())).thenReturn(Optional.of(TEST_PROJECT));
        when(userRepository.findById(TEST_USER_2.getUsername())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.deleteProjectUser(TEST_PROJECT.getUuid(), TEST_USER.getUsername(), TEST_USER_2.getUsername()))
                .hasMessage(ExceptionMessages.User.userNotFound(TEST_USER_2.getUsername()))
                .isExactlyInstanceOf(UserNotFoundException.class);
    }

    @Test
    void testDeleteProjectUser_ThrowsIfInitiatorIsNotTeamLead() {
        when(projectRepository.findById(TEST_PROJECT.getUuid())).thenReturn(Optional.of(TEST_PROJECT));
        when(userRepository.findById(TEST_USER_2.getUsername())).thenReturn(Optional.of(TEST_USER_2));
        when(projectRepository.isUserInProject(TEST_USER_2, TEST_PROJECT, Role.TEAM_LEAD)).thenReturn(false);

        assertThatThrownBy(() -> projectService.deleteProjectUser(TEST_PROJECT.getUuid(), TEST_USER.getUsername(), TEST_USER_2.getUsername()))
                .hasMessage(ExceptionMessages.ProjectUser.cannotRemoveUserFromProject(TEST_PROJECT.getUuid(), TEST_USER_2.getUsername()))
                .isExactlyInstanceOf(UnauthorizedProjectModificationException.class);
    }

    @Test
    void testDeleteProjectUser_ThrowsOnNonexistentUser() {
        when(projectRepository.findById(TEST_PROJECT.getUuid())).thenReturn(Optional.of(TEST_PROJECT));
        when(userRepository.findById(TEST_USER_2.getUsername())).thenReturn(Optional.of(TEST_USER_2));
        when(projectRepository.isUserInProject(TEST_USER_2, TEST_PROJECT, Role.TEAM_LEAD)).thenReturn(true);
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.deleteProjectUser(TEST_PROJECT.getUuid(), TEST_USER.getUsername(), TEST_USER_2.getUsername()))
                .hasMessage(ExceptionMessages.User.userNotFound(TEST_USER.getUsername()))
                .isExactlyInstanceOf(UserNotFoundException.class);
    }


    @Test
    void testDeleteProjectUser_ThrowsOnUserNotInProject() {
        when(projectRepository.findById(TEST_PROJECT.getUuid())).thenReturn(Optional.of(TEST_PROJECT));
        when(userRepository.findById(TEST_USER_2.getUsername())).thenReturn(Optional.of(TEST_USER_2));
        when(projectRepository.isUserInProject(TEST_USER_2, TEST_PROJECT, Role.TEAM_LEAD)).thenReturn(true);
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));
        when(projectRepository.isUserInProject(TEST_USER, TEST_PROJECT)).thenReturn(false);

        assertThatThrownBy(() -> projectService.deleteProjectUser(TEST_PROJECT.getUuid(), TEST_USER.getUsername(), TEST_USER_2.getUsername()))
                .hasMessage(ExceptionMessages.ProjectUser.userNotFound(TEST_USER.getUsername(), TEST_PROJECT.getUuid()))
                .isExactlyInstanceOf(UserNotPartOfProjectException.class);
    }

    @Test
    void testDeleteProjectUser_SuccessfullyDeletesUser() {
        when(projectRepository.findById(TEST_PROJECT.getUuid())).thenReturn(Optional.of(TEST_PROJECT));
        when(userRepository.findById(TEST_USER_2.getUsername())).thenReturn(Optional.of(TEST_USER_2));
        when(projectRepository.isUserInProject(TEST_USER_2, TEST_PROJECT, Role.TEAM_LEAD)).thenReturn(true);
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));
        when(projectRepository.isUserInProject(TEST_USER, TEST_PROJECT)).thenReturn(true);

        projectService.deleteProjectUser(TEST_PROJECT.getUuid(), TEST_USER.getUsername(), TEST_USER_2.getUsername());

        verify(projectUserRepository, times(1)).deleteAllByProjectAndUser(TEST_PROJECT, TEST_USER);
    }

    @Test
    void testGetAllProjects_ReturnsEmptyList() {
        when(projectRepository.findAll()).thenReturn(List.of());

        var result = projectService.getAllProjects();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllProjects_ReturnsProjectList() {
        when(projectRepository.findAll()).thenReturn(List.of(TEST_PROJECT, TEST_PROJECT_2));

        var result = projectService.getAllProjects();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testFindProjectById_ThrowsOnNonexistentProject() {
        String projectId = "non-existent";
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.findProjectById(projectId))
                .hasMessage(ExceptionMessages.Project.projectNotFound(projectId))
                .isExactlyInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    void testFindProjectById_ReturnsProjectDTO() {
        when(projectRepository.findById(TEST_PROJECT.getUuid())).thenReturn(Optional.of(TEST_PROJECT));

        var result = projectService.findProjectById(TEST_PROJECT.getUuid());

        assertNotNull(result);
        assertEquals(TEST_PROJECT.getUuid(), result.uuid());
        assertEquals(TEST_PROJECT.getName(), result.name());
    }

    @Test
    void testAddProject_SuccessfullyCreatesProject() {
        Project savedProject = new Project(CREATE_PROJECT_DTO.name());
        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));

        projectService.addProject(CREATE_PROJECT_DTO, TEST_USER.getUsername());

        verify(projectRepository, times(2)).save(any(Project.class));
        verify(neoProjectRepository, times(1)).addProject(any());
    }

    @Test
    void testUpdateProject_ThrowsOnNonexistentProject() {
        String projectId = "non-existent";
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.updateProject(projectId, UPDATE_PROJECT_DTO))
                .hasMessage(ExceptionMessages.Project.projectNotFound(projectId))
                .isExactlyInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    void testUpdateProject_SuccessfullyUpdatesProject() {
        when(projectRepository.findById(TEST_PROJECT.getUuid())).thenReturn(Optional.of(TEST_PROJECT));

        projectService.updateProject(TEST_PROJECT.getUuid(), UPDATE_PROJECT_DTO);

        verify(projectMapper, times(1)).patchProjectFromDTO(UPDATE_PROJECT_DTO, TEST_PROJECT);
        verify(projectRepository, times(1)).save(TEST_PROJECT);
    }

    @Test
    void testAddProjectWorkflow_ThrowsOnNonexistentProject() {
        String projectId = "non-existent";
        ProjectWorkflowDTO dto = new ProjectWorkflowDTO(List.of("To do", "Done"), "To do",
                List.of(new WorkflowTransitionDTO("To do", "Done")));

        when(projectRepository.existsById(projectId)).thenReturn(false);

        assertThatThrownBy(() -> projectService.addProjectWorkflow(projectId, dto))
                .hasMessage(ExceptionMessages.Project.projectNotFound(projectId))
                .isExactlyInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    void testAddProjectWorkflow_ThrowsWhenTransitionSourceIsInvalid() {
        ProjectWorkflowDTO dto = new ProjectWorkflowDTO(List.of("To do", "Done"), "To do",
                List.of(new WorkflowTransitionDTO("Backlog", "Done")));

        when(projectRepository.existsById(TEST_PROJECT.getUuid())).thenReturn(true);

        assertThatThrownBy(() -> projectService.addProjectWorkflow(TEST_PROJECT.getUuid(), dto))
                .hasMessage(ExceptionMessages.Project.invalidSourceStatus(dto.workflowStatuses()))
                .isExactlyInstanceOf(InvalidWorkflowException.class);
    }

    @Test
    void testAddProjectWorkflow_ThrowsWhenTransitionTargetIsInvalid() {
        ProjectWorkflowDTO dto = new ProjectWorkflowDTO(List.of("To do", "Done"), "To do",
                List.of(new WorkflowTransitionDTO("To do", "Blocked")));

        when(projectRepository.existsById(TEST_PROJECT.getUuid())).thenReturn(true);

        assertThatThrownBy(() -> projectService.addProjectWorkflow(TEST_PROJECT.getUuid(), dto))
                .hasMessage(ExceptionMessages.Project.invalidTargetStatus(dto.workflowStatuses()))
                .isExactlyInstanceOf(InvalidWorkflowException.class);
    }

    @Test
    void testAddProjectWorkflow_ThrowsWhenSourceAndTargetMatch() {
        ProjectWorkflowDTO dto = new ProjectWorkflowDTO(List.of("To do", "Done"), "To do",
                List.of(new WorkflowTransitionDTO("To do", "To do")));

        when(projectRepository.existsById(TEST_PROJECT.getUuid())).thenReturn(true);

        assertThatThrownBy(() -> projectService.addProjectWorkflow(TEST_PROJECT.getUuid(), dto))
                .hasMessage(ExceptionMessages.Project.transitionBuckle())
                .isExactlyInstanceOf(InvalidWorkflowException.class);
    }

    @Test
    void testAddProjectWorkflow_SuccessfullyCreatesWorkflow() {
        ProjectWorkflowDTO dto = new ProjectWorkflowDTO(List.of("To do", "Done"), "To do",
                List.of(new WorkflowTransitionDTO("To do", "Done")));

        when(projectRepository.existsById(TEST_PROJECT.getUuid())).thenReturn(true);

        projectService.addProjectWorkflow(TEST_PROJECT.getUuid(), dto);

        verify(workflowRepository, times(1)).createWorkflow(TEST_PROJECT.getUuid(), dto);
    }

    @Test
    void testDeleteProjectWorkflow_ThrowsOnNonexistentProject() {
        String projectId = "non-existent";
        when(projectRepository.existsById(projectId)).thenReturn(false);

        assertThatThrownBy(() -> projectService.deleteProjectWorkflow(projectId))
                .hasMessage(ExceptionMessages.Project.projectNotFound(projectId))
                .isExactlyInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    void testDeleteProjectWorkflow_SuccessfullyDeletesWorkflow() {
        when(projectRepository.existsById(TEST_PROJECT.getUuid())).thenReturn(true);

        projectService.deleteProjectWorkflow(TEST_PROJECT.getUuid());

        verify(workflowRepository, times(1)).deleteWorkflow(TEST_PROJECT.getUuid());
    }

    @Test
    void testDeleteProject_ThrowsOnNonexistentProject() {
        String projectId = "non-existent";
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.deleteProject(projectId))
                .hasMessage(ExceptionMessages.Project.projectNotFound(projectId))
                .isExactlyInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    void testDeleteProject_SuccessfullyDeletesProject() {
        when(projectRepository.findById(TEST_PROJECT.getUuid())).thenReturn(Optional.of(TEST_PROJECT));

        projectService.deleteProject(TEST_PROJECT.getUuid());

        verify(projectRepository, times(1)).delete(TEST_PROJECT);
    }
}

