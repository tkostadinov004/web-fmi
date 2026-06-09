package bg.sofia.uni.fmi.issuetracker.controller;

import bg.sofia.uni.fmi.issuetracker.dto.input.auth.ChangeForgottenPasswordDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.auth.ChangePasswordDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.auth.SendForgotPasswordEmailDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.auth.UserLoginDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.auth.UserRegisterDTO;
import bg.sofia.uni.fmi.issuetracker.exception.auth.AlreadyChangedPasswordException;
import bg.sofia.uni.fmi.issuetracker.exception.auth.ForgotPasswordTokenAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.exception.auth.UserAlreadyLoggedException;
import bg.sofia.uni.fmi.issuetracker.exception.auth.WrongCredentialsException;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserNotFoundException;
import bg.sofia.uni.fmi.issuetracker.response.AuthResponse;
import bg.sofia.uni.fmi.issuetracker.service.contract.AuthService;
import bg.sofia.uni.fmi.issuetracker.service.contract.FeatureFlagService;
import bg.sofia.uni.fmi.issuetracker.utils.Constants;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import bg.sofia.uni.fmi.issuetracker.utils.messages.OutputMessages;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AuthController.class})
public class AuthControllerTests extends BaseControllerTests {
    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private FeatureFlagService featureFlagService;

    @Test
    public void testRegister_ReturnsBadRequestOnInvalidData() throws Exception {
        UserRegisterDTO invalid = new UserRegisterDTO("", "", "User", "emailemail.com", "company", "");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.firstName").exists())
                .andExpect(jsonPath("$.errors.lastName").exists())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.password").exists());
    }

    @Test
    public void testRegister_ReturnsOkWhenSuccessful() throws Exception {
        UserRegisterDTO dto = new UserRegisterDTO("firstName", "lastName", "User", "email@email.com", "company", "pass");
        when(authService.register(dto)).thenReturn(new AuthResponse(OutputMessages.Auth.SUCCESSFULLY_CREATED_USER, null));

        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content(OBJECT_MAPPER.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(OutputMessages.Auth.SUCCESSFULLY_CREATED_USER))
                .andExpect(jsonPath("$.token").value(nullValue()));
    }

    @Test
    public void testRegister_ReturnsConflictWhenUserAlreadyExists() throws Exception {
        UserRegisterDTO dto = new UserRegisterDTO("firstName", "lastName", "User", "email@email.com", "company", "pass");
        when(authService.register(dto)).thenThrow(new UserAlreadyExistsException(ExceptionMessages.User.userAlreadyExists(dto.username())));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.User.userAlreadyExists(dto.username())));
    }

    @Test
    public void testLogin_ReturnsOkWhenSuccessful() throws Exception {
        UserLoginDTO dto = new UserLoginDTO("User", "pass");
        when(authService.login(dto)).thenReturn("token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "token"));
    }

    @Test
    public void testLogin_ReturnsBadRequestWhenWrongCredentials() throws Exception {
        UserLoginDTO dto = new UserLoginDTO("User", "wrong");
        when(authService.login(dto)).thenThrow(new WrongCredentialsException(ExceptionMessages.Auth.wrongCredentials()));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.Auth.wrongCredentials()));
    }

    @Test
    public void testLogin_ReturnsConflictWhenAlreadyLogged() throws Exception {
        UserLoginDTO dto = new UserLoginDTO("User", "pass");
        when(authService.login(dto)).thenThrow(new UserAlreadyLoggedException(ExceptionMessages.Auth.userAlreadyLoggedIn(dto.username())));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.Auth.userAlreadyLoggedIn(dto.username())));
    }

    @Test
    public void testLogout_ReturnsNoContentWhenSuccessful() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", null));

        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testLogout_ReturnsNotFoundWhenMissing() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", null));
        doThrow(new UserNotFoundException(ExceptionMessages.User.userNotFound("user"))).when(authService).logout("user");

        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.User.userNotFound("user")));
    }

    @Test
    public void testChangePassword_ReturnsNoContentWhenSuccessful() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", null));
        ChangePasswordDTO dto = new ChangePasswordDTO("old", "new", "new");

        mockMvc.perform(patch("/auth/password")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testChangePassword_ReturnsBadRequestWhenWrongOldPassword() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", null));
        ChangePasswordDTO dto = new ChangePasswordDTO("old", "new", "new");
        doThrow(new WrongCredentialsException(ExceptionMessages.Auth.wrongOldPassword())).when(authService).changePassword("user", dto);

        mockMvc.perform(patch("/auth/password")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.Auth.wrongOldPassword()));
    }

    @Test
    public void testSendForgotPasswordEmail_ReturnsAcceptedWithResetTokenHeaderWhenFeatureFlagEnabled() throws Exception {
        SendForgotPasswordEmailDTO dto = new SendForgotPasswordEmailDTO("email@test.com", "url");
        when(featureFlagService.isFeatureEnabled(Constants.SKIP_EMAIL_FEATURE_FLAG)).thenReturn(true);
        when(authService.sendForgotPasswordEmail(dto)).thenReturn("reset-token");

        mockMvc.perform(post("/auth/forgotPassword")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(dto)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.error").doesNotExist())
                .andExpect(result -> assertEquals("reset-token", result.getResponse().getHeader("Reset-Token")));
    }

    @Test
    public void testSendForgotPasswordEmail_ReturnsConflictWhenTokenAlreadyExists() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", null));
        SendForgotPasswordEmailDTO dto = new SendForgotPasswordEmailDTO("email@test.com", "url");
        when(featureFlagService.isFeatureEnabled(Constants.SKIP_EMAIL_FEATURE_FLAG)).thenReturn(false);
        doThrow(new ForgotPasswordTokenAlreadyExistsException(ExceptionMessages.Auth.forgotPasswordTokenAlreadyExists())).when(authService).sendForgotPasswordEmail(dto);

        mockMvc.perform(post("/auth/forgotPassword")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.Auth.forgotPasswordTokenAlreadyExists()));
    }

    @Test
    public void testChangeForgottenPassword_ReturnsNoContentWhenSuccessful() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", null));
        ChangeForgottenPasswordDTO dto = new ChangeForgottenPasswordDTO("token", "new", "new");

        mockMvc.perform(patch("/auth/forgotPassword")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testChangeForgottenPassword_ReturnsConflictWhenTokenInvalid() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", null));
        ChangeForgottenPasswordDTO dto = new ChangeForgottenPasswordDTO("token", "new", "new");
        doThrow(new AlreadyChangedPasswordException("Reset token is invalid or already used")).when(authService).changeForgottenPassword(dto);

        mockMvc.perform(patch("/auth/forgotPassword")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Reset token is invalid or already used"));
    }
}
