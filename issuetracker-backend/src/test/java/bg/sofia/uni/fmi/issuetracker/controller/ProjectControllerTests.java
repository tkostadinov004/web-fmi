package bg.sofia.uni.fmi.issuetracker.controller;

import bg.sofia.uni.fmi.issuetracker.dto.input.project.CreateProjectDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.project.CreateProjectUserDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.project.UpdateProjectDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.project.ProjectDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.project.ProjectDetailsUserDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.ticket.TicketDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectNotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectUserAlreadyInProjectException;
import bg.sofia.uni.fmi.issuetracker.exception.project.UserNotPartOfProjectException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketNotFoundException;
import bg.sofia.uni.fmi.issuetracker.model.auth.Role;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketPriority;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketStatus;
import bg.sofia.uni.fmi.issuetracker.service.contract.ProjectService;
import bg.sofia.uni.fmi.issuetracker.service.contract.ticket.TicketService;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static bg.sofia.uni.fmi.issuetracker.TestData.CREATE_PROJECT_DTO;
import static bg.sofia.uni.fmi.issuetracker.TestData.CREATE_PROJECT_USER_DTO;
import static bg.sofia.uni.fmi.issuetracker.TestData.TEST_PROJECT;
import static bg.sofia.uni.fmi.issuetracker.TestData.TEST_PROJECT_2;
import static bg.sofia.uni.fmi.issuetracker.TestData.TEST_USER;
import static bg.sofia.uni.fmi.issuetracker.TestData.TEST_USER_2;
import static bg.sofia.uni.fmi.issuetracker.TestData.UPDATE_PROJECT_DTO;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ProjectController.class})
public class ProjectControllerTests extends BaseControllerTests {
    @MockitoBean
    private TicketService ticketService;

    @MockitoBean
    private ProjectService projectService;

    @Test
    public void testGetAllProjects_ReturnsOk() throws Exception {
        ProjectDetailsDTO dto1 = ProjectDetailsDTO.from(TEST_PROJECT);
        ProjectDetailsDTO dto2 = ProjectDetailsDTO.from(TEST_PROJECT_2);
        when(projectService.getAllProjects()).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uuid").value(TEST_PROJECT.getUuid()))
                .andExpect(jsonPath("$[1].uuid").value(TEST_PROJECT_2.getUuid()));
    }

    @Test
    public void testGetProjectByProjectId_ReturnsOk() throws Exception {
        ProjectDetailsDTO dto = ProjectDetailsDTO.from(TEST_PROJECT);
        when(projectService.findProjectById(TEST_PROJECT.getUuid())).thenReturn(dto);

        mockMvc.perform(get("/projects/%s".formatted(TEST_PROJECT.getUuid())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(TEST_PROJECT.getUuid()))
                .andExpect(jsonPath("$.name").value(TEST_PROJECT.getName()));
    }

    @Test
    public void testGetProjectByProjectId_ReturnsNotFoundWhenMissing() throws Exception {
        String projectId = "non-existent";
        when(projectService.findProjectById(projectId))
                .thenThrow(new ProjectNotFoundException(ExceptionMessages.Project.projectNotFound(projectId)));

        mockMvc.perform(get("/projects/%s".formatted(projectId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.Project.projectNotFound(projectId)));
    }

    @Test
    public void testCreateProject_ReturnsCreatedWhenSuccessful() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", null));
        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(CREATE_PROJECT_DTO)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreateProject_ReturnsBadRequestOnInvalidData() throws Exception {
        CreateProjectDTO invalid = new CreateProjectDTO("");

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    public void testCreateProject_ReturnsConflictWhenProjectAlreadyExists() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", null));
        doThrow(new ProjectAlreadyExistsException(ExceptionMessages.Project.projectAlreadyExists(CREATE_PROJECT_DTO.name())))
                .when(projectService).addProject(any(), any());

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(CREATE_PROJECT_DTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.Project.projectAlreadyExists(CREATE_PROJECT_DTO.name())));
    }

    @Test
    public void testUpdateProject_ReturnsNoContentWhenSuccessful() throws Exception {
        mockMvc.perform(patch("/projects/%s".formatted(TEST_PROJECT.getUuid()))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(UPDATE_PROJECT_DTO)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testUpdateProject_ReturnsBadRequestOnInvalidData() throws Exception {
        UpdateProjectDTO invalid = new UpdateProjectDTO(
                "x".repeat(501),
                null
        );

        mockMvc.perform(patch("/projects/%s".formatted(TEST_PROJECT.getUuid()))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    public void testUpdateProject_ReturnsNotFoundWhenProjectMissing() throws Exception {
        String projectId = "non-existent";
        doThrow(new ProjectNotFoundException(ExceptionMessages.Project.projectNotFound(projectId)))
                .when(projectService).updateProject(eq(projectId), any(UpdateProjectDTO.class));

        mockMvc.perform(patch("/projects/%s".formatted(projectId))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(UPDATE_PROJECT_DTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.Project.projectNotFound(projectId)));
    }

    @Test
    public void testDeleteProject_ReturnsNoContentWhenSuccessful() throws Exception {
        mockMvc.perform(delete("/projects/%s".formatted(TEST_PROJECT.getUuid())))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteProject_ReturnsNotFoundWhenProjectMissing() throws Exception {
        String projectId = "non-existent";
        doThrow(new ProjectNotFoundException(ExceptionMessages.Project.projectNotFound(projectId)))
                .when(projectService).deleteProject(projectId);

        mockMvc.perform(delete("/projects/%s".formatted(projectId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.Project.projectNotFound(projectId)));
    }

    @Test
    public void testGetAllTicketsByProject_ReturnsOk() throws Exception {
        TicketDetailsDTO dto = new TicketDetailsDTO("Ticket-1", "Title", "Description", TicketStatus.IN_PROGRESS,
                TicketPriority.HIGH, LocalDateTime.now(), LocalDateTime.now(), null, "project-uuid", null, List.of());
        when(ticketService.getAllTicketsByProject(dto.projectUuid())).thenReturn(List.of(dto));

        mockMvc.perform(get("/projects/%s/tickets".formatted(dto.projectUuid())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value(dto.code()));
    }

    @Test
    public void testGetAllTicketsByProject_ReturnsNotFoundWhenMissing() throws Exception {
        when(ticketService.getAllTicketsByProject(TEST_PROJECT.getUuid()))
                .thenThrow(new TicketNotFoundException(ExceptionMessages.Project.projectNotFound(TEST_PROJECT.getUuid())));

        mockMvc.perform(get("/projects/%s/tickets".formatted(TEST_PROJECT.getUuid())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.Project.projectNotFound(TEST_PROJECT.getUuid())));
    }

    @Test
    public void testGetAssignedUsers_ReturnsOk() throws Exception {
        ProjectDetailsUserDTO userDto1 = new ProjectDetailsUserDTO(null, TEST_USER.getUsername(), Set.of(Role.TEAM_LEAD));
        ProjectDetailsUserDTO userDto2 = new ProjectDetailsUserDTO(null, TEST_USER_2.getUsername(), Set.of(Role.TEAM_LEAD));
        when(projectService.getProjectUsers(TEST_PROJECT.getUuid()))
                .thenReturn(List.of(userDto1, userDto2));

        mockMvc.perform(get("/projects/%s/users".formatted(TEST_PROJECT.getUuid())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value(TEST_USER.getUsername()))
                .andExpect(jsonPath("$[1].username").value(TEST_USER_2.getUsername()));
    }

    @Test
    public void testGetAssignedUsers_ReturnsNotFoundWhenProjectMissing() throws Exception {
        String projectId = "non-existent";
        when(projectService.getProjectUsers(projectId))
                .thenThrow(new ProjectNotFoundException(ExceptionMessages.Project.projectNotFound(projectId)));

        mockMvc.perform(get("/projects/%s/users".formatted(projectId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.Project.projectNotFound(projectId)));
    }

    @Test
    public void testAddUserToProject_ReturnsCreatedWhenSuccessful() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", null));
        ProjectDetailsUserDTO resultDto = new ProjectDetailsUserDTO(null, TEST_USER_2.getUsername(), Set.of(Role.TEAM_LEAD));
        when(projectService.addProjectUser(eq(TEST_PROJECT.getUuid()), any(CreateProjectUserDTO.class), anyString()))
                .thenReturn(resultDto);

        mockMvc.perform(post("/projects/%s/users".formatted(TEST_PROJECT.getUuid()))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(CREATE_PROJECT_USER_DTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(TEST_USER_2.getUsername()));
    }

    @Test
    public void testAddUserToProject_ReturnsConflictWhenUserAlreadyInProject() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", null));
        when(projectService.addProjectUser(eq(TEST_PROJECT.getUuid()), any(CreateProjectUserDTO.class), anyString()))
                .thenThrow(new ProjectUserAlreadyInProjectException(
                        ExceptionMessages.ProjectUser.userAlreadyInProject(TEST_USER_2.getUsername(), TEST_PROJECT.getUuid(), CREATE_PROJECT_USER_DTO.role())));

        mockMvc.perform(post("/projects/%s/users".formatted(TEST_PROJECT.getUuid()))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(CREATE_PROJECT_USER_DTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    public void testRemoveUserFromProject_ReturnsNoContentWhenSuccessful() throws Exception {
        mockMvc.perform(delete("/projects/%s/users/%s".formatted(TEST_PROJECT.getUuid(), TEST_USER.getUsername())))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testRemoveUserFromProject_ReturnsNotFoundWhenProjectOrUserMissing() throws Exception {
        String projectId = "non-existent";
        doThrow(new ProjectNotFoundException(ExceptionMessages.Project.projectNotFound(projectId)))
                .when(projectService).deleteProjectUser(projectId, TEST_USER.getUsername());

        mockMvc.perform(delete("/projects/%s/users/%s".formatted(projectId, TEST_USER.getUsername())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.Project.projectNotFound(projectId)));
    }

    @Test
    public void testUnauthorizedRequest_ReturnsUnauthorized() throws Exception {
        super.unauthorizedRequest(get("/projects"));
    }
}

