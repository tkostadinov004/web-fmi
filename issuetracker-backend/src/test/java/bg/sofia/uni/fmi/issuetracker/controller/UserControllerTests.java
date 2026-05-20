package bg.sofia.uni.fmi.issuetracker.controller;

import bg.sofia.uni.fmi.issuetracker.controller.common.AuthorizationAspect;
import bg.sofia.uni.fmi.issuetracker.dto.input.UpdateUserDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.AdminOnlyOutputUserDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.UserDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.auditlog.OutputAuditLogDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.auditlog.OutputAuditLogUserDTO;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserNotFoundException;
import bg.sofia.uni.fmi.issuetracker.service.contract.AuditLogService;
import bg.sofia.uni.fmi.issuetracker.service.contract.ProjectService;
import bg.sofia.uni.fmi.issuetracker.service.contract.UserService;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import bg.sofia.uni.fmi.issuetracker.utils.messages.OutputMessages;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UserController.class})
@Import(AuthorizationAspect.class)
@EnableAspectJAutoProxy
public class UserControllerTests extends BaseControllerTests {
    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ProjectService projectService;

    @MockitoBean
    private AuditLogService auditLogService;

    @Test
    public void testGetAllUsers_ReturnsOk() throws Exception {
        List<AdminOnlyOutputUserDTO> content = List.of(new AdminOnlyOutputUserDTO("user", "First", "Last", "email@test.com", true, false));
        Page<AdminOnlyOutputUserDTO> page = spy(new PageImpl<>(content));
        when(page.getSort()).thenReturn(Sort.by(Sort.Direction.ASC, "username"));

        when(userService.isAdmin("admin")).thenReturn(true);
        when(userService.getAllUsers(1, 10, "username", true))
                .thenReturn(page);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", null));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(header().string("Link", containsString("rel=")))
                .andExpect(jsonPath("$[0].username").value("user"));
    }

    @Test
    public void testGetAllUsers_ReturnsForbiddenWhenNotAdmin() throws Exception {
        when(userService.isAdmin("user")).thenReturn(false);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", null));

        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value(OutputMessages.System.ACCESS_DENIED));
    }

    @Test
    public void testDeleteUser_ReturnsNoContentWhenSuccessful() throws Exception {
        when(userService.isAdmin("admin")).thenReturn(true);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", null));

        mockMvc.perform(delete("/users/user"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteUser_ReturnsNotFoundWhenMissing() throws Exception {
        when(userService.isAdmin("admin")).thenReturn(true);
        doThrow(new UserNotFoundException(ExceptionMessages.User.userNotFound("missing"))).when(userService).deleteUser("missing");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", null));

        mockMvc.perform(delete("/users/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.User.userNotFound("missing")));
    }

    @Test
    public void testChangeProfilePicture_ReturnsNoContentWhenSuccessful() throws Exception {
        when(userService.isAdmin("user")).thenReturn(false);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", null));
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", MediaType.IMAGE_PNG_VALUE, "data".getBytes());

        mockMvc.perform(multipart(HttpMethod.PATCH, "/users/profilePictures").file(file))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testChangeProfilePicture_ReturnsBadRequestOnInvalidFileFormat() throws Exception {
        when(userService.isAdmin("user")).thenReturn(false);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", null));
        MockMultipartFile file = new MockMultipartFile("file", "avatar.txt", MediaType.TEXT_PLAIN_VALUE, "data".getBytes());

        mockMvc.perform(multipart(HttpMethod.PATCH, "/users/profilePictures").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.File.invalidFormat()));
    }

    @Test
    public void testGetUserDetails_ReturnsOk() throws Exception {
        UserDetailsDTO dto = new UserDetailsDTO(null, "user", "First", "Last", "email@test.com", "company", false, List.of());
        when(userService.getUser("user")).thenReturn(dto);

        mockMvc.perform(get("/users/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"));
    }

    @Test
    public void testGetUserDetails_ReturnsNotFoundWhenMissing() throws Exception {
        when(userService.getUser("missing")).thenThrow(new UserNotFoundException(ExceptionMessages.User.userNotFound("missing")));

        mockMvc.perform(get("/users/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.User.userNotFound("missing")));
    }

    @Test
    public void testGetAuditLogs_ReturnsOk() throws Exception {
        List<OutputAuditLogDTO> content = List.of(
                new OutputAuditLogDTO("a1b2c3", "User logged in", null, LocalDateTime.of(2026, 5, 20, 12, 0),
                        new OutputAuditLogUserDTO("user", "/files/user/profile.png")));
        Page<OutputAuditLogDTO> page = new PageImpl<>(content);
        when(auditLogService.getAll("user", 1, 10)).thenReturn(page);

        mockMvc.perform(get("/users/user/auditLogs"))
                .andExpect(status().isOk())
                .andExpect(header().string("Link", containsString("rel=")))
                .andExpect(jsonPath("$[0].uuid").value("a1b2c3"))
                .andExpect(jsonPath("$[0].message").value("User logged in"))
                .andExpect(jsonPath("$[0].user.username").value("user"));
    }

    @Test
    public void testGetAuditLogs_ReturnsNotFoundWhenUserMissing() throws Exception {
        when(auditLogService.getAll("missing", 1, 10))
                .thenThrow(new UserNotFoundException(ExceptionMessages.User.userNotFound("missing")));

        mockMvc.perform(get("/users/missing/auditLogs"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.User.userNotFound("missing")));
    }

    @Test
    public void testPatchUser_ReturnsNoContentWhenSuccessful() throws Exception {
        UpdateUserDTO dto = new UpdateUserDTO("First", "Last", "email@test.com", "Company");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", null));

        mockMvc.perform(patch("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testPatchUser_ReturnsBadRequestOnInvalidInput() throws Exception {
        String longName = "x".repeat(201);
        UpdateUserDTO invalid = new UpdateUserDTO(longName, null, null, null);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", null));

        mockMvc.perform(patch("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.firstName").exists());
    }

    @Test
    public void testPatchUser_ReturnsConflictWhenEmailTaken() throws Exception {
        UpdateUserDTO dto = new UpdateUserDTO("First", "Last", "email@test.com", "Company");
        doThrow(new UserAlreadyExistsException(ExceptionMessages.User.emailAlreadyExists(dto.email()))).when(userService).updateUser("user", dto);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", null));

        mockMvc.perform(patch("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.User.emailAlreadyExists(dto.email())));
    }

    @Test
    public void testUnauthorizedRequest_ReturnsUnauthorized() throws Exception {
        super.unauthorizedRequest(get("/users"));
    }
}
