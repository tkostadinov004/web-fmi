package bg.sofia.uni.fmi.issuetracker.controller;

import bg.sofia.uni.fmi.issuetracker.controller.common.AuthorizationAspect;
import bg.sofia.uni.fmi.issuetracker.dto.input.featureflag.AddFeatureFlagDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.OutputFeatureFlagDTO;
import bg.sofia.uni.fmi.issuetracker.exception.featureflag.FeatureFlagAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.exception.featureflag.FeatureFlagNotFoundException;
import bg.sofia.uni.fmi.issuetracker.service.contract.FeatureFlagService;
import bg.sofia.uni.fmi.issuetracker.service.contract.ProjectService;
import bg.sofia.uni.fmi.issuetracker.service.contract.UserService;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import bg.sofia.uni.fmi.issuetracker.utils.messages.OutputMessages;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {FeatureFlagController.class})
@Import(AuthorizationAspect.class)
@EnableAspectJAutoProxy
public class FeatureFlagControllerTests extends BaseControllerTests {
    @MockitoBean
    private FeatureFlagService featureFlagService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ProjectService projectService;

    @Test
    public void testGetAll_ReturnsOk() throws Exception {
        when(userService.isAdmin("admin")).thenReturn(true);
        when(featureFlagService.getAll()).thenReturn(List.of(new OutputFeatureFlagDTO("feature", true)));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", null));

        mockMvc.perform(get("/featureflags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("feature"))
                .andExpect(jsonPath("$[0].value").value(true));
    }

    @Test
    public void testGetAll_ReturnsForbiddenWhenNotAdmin() throws Exception {
        when(userService.isAdmin("user")).thenReturn(false);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", null));

        mockMvc.perform(get("/featureflags"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value(OutputMessages.System.ACCESS_DENIED));
    }

    @Test
    public void testGetValue_ReturnsOk() throws Exception {
        when(userService.isAdmin("admin")).thenReturn(true);
        when(featureFlagService.getFeatureFlag("feature")).thenReturn(new OutputFeatureFlagDTO("feature", false));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", null));

        mockMvc.perform(get("/featureflags/feature"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("feature"))
                .andExpect(jsonPath("$.value").value(false));
    }

    @Test
    public void testGetValue_ReturnsNotFoundWhenMissing() throws Exception {
        when(userService.isAdmin("admin")).thenReturn(true);
        when(featureFlagService.getFeatureFlag("missing")).thenThrow(new FeatureFlagNotFoundException(ExceptionMessages.FeatureFlag.featureFlagNotFound("missing")));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", null));

        mockMvc.perform(get("/featureflags/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.FeatureFlag.featureFlagNotFound("missing")));
    }

    @Test
    public void testCreateFeatureFlag_ReturnsNoContentWhenSuccessful() throws Exception {
        when(userService.isAdmin("admin")).thenReturn(true);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", null));
        AddFeatureFlagDTO dto = new AddFeatureFlagDTO("new-flag", "true");

        mockMvc.perform(post("/featureflags")
                        .contentType("application/json")
                        .content(OBJECT_MAPPER.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testCreateFeatureFlag_ReturnsBadRequestOnInvalidData() throws Exception {
        when(userService.isAdmin("admin")).thenReturn(true);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", null));
        AddFeatureFlagDTO invalid = new AddFeatureFlagDTO("", "notBoolean");

        mockMvc.perform(post("/featureflags")
                        .contentType("application/json")
                        .content(OBJECT_MAPPER.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.value").exists());
    }

    @Test
    public void testCreateFeatureFlag_ReturnsConflictWhenAlreadyExists() throws Exception {
        when(userService.isAdmin("admin")).thenReturn(true);
        AddFeatureFlagDTO dto = new AddFeatureFlagDTO("existing", "false");
        doThrow(new FeatureFlagAlreadyExistsException(ExceptionMessages.FeatureFlag.featureFlagAlreadyExists(dto.name()))).when(featureFlagService).addFeatureFlag(dto);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", null));

        mockMvc.perform(post("/featureflags")
                        .contentType("application/json")
                        .content(OBJECT_MAPPER.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.FeatureFlag.featureFlagAlreadyExists(dto.name())));
    }

    @Test
    public void testSetFeatureFlagValue_ReturnsNoContentWhenSuccessful() throws Exception {
        when(userService.isAdmin("admin")).thenReturn(true);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", null));

        mockMvc.perform(patch("/featureflags/feature").param("new_value", "true"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testSetFeatureFlagValue_ReturnsNotFoundWhenMissing() throws Exception {
        when(userService.isAdmin("admin")).thenReturn(true);
        doThrow(new FeatureFlagNotFoundException(ExceptionMessages.FeatureFlag.featureFlagNotFound("missing"))).when(featureFlagService).setFeatureFlagValue("missing", true);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", null));

        mockMvc.perform(patch("/featureflags/missing").param("new_value", "true"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.FeatureFlag.featureFlagNotFound("missing")));
    }

    @Test
    public void testDeleteFeatureFlag_ReturnsNoContentWhenSuccessful() throws Exception {
        when(userService.isAdmin("admin")).thenReturn(true);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", null));

        mockMvc.perform(delete("/featureflags/feature"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteFeatureFlag_ReturnsNotFoundWhenMissing() throws Exception {
        when(userService.isAdmin("admin")).thenReturn(true);
        doThrow(new FeatureFlagNotFoundException(ExceptionMessages.FeatureFlag.featureFlagNotFound("missing"))).when(featureFlagService).deleteFeatureFlag("missing");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", null));

        mockMvc.perform(delete("/featureflags/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.FeatureFlag.featureFlagNotFound("missing")));
    }

    @Test
    public void testUnauthorizedRequest_ReturnsUnauthorized() throws Exception {
        super.unauthorizedRequest(get("/featureflags"));
    }
}
