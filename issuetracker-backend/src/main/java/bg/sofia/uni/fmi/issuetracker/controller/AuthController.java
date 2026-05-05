package bg.sofia.uni.fmi.issuetracker.controller;

import bg.sofia.uni.fmi.issuetracker.dto.input.auth.ChangeForgottenPasswordDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.auth.ChangePasswordDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.auth.SendForgotPasswordEmailDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.auth.UserLoginDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.auth.UserRegisterDTO;
import bg.sofia.uni.fmi.issuetracker.response.AuthResponse;
import bg.sofia.uni.fmi.issuetracker.service.contract.AuthService;
import bg.sofia.uni.fmi.issuetracker.service.contract.FeatureFlagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for registration, login, logout and password recovery")
public class AuthController {
    private final AuthService authService;
    private final Environment environment;
    private final FeatureFlagService featureFlagService;

    public AuthController(AuthService authService, Environment environment, FeatureFlagService featureFlagService) {
        this.authService = authService;
        this.environment = environment;
        this.featureFlagService = featureFlagService;
    }

    @Operation(summary = "Register a new user", description = "Creates a new account for a user and returns a success message.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User created successfully"),
            @ApiResponse(responseCode = "409", description = "A user with the provided username already exists"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRegisterDTO user) {
        return ResponseEntity.ok(authService.register(user));
    }

    @Operation(summary = "Authenticate a user", description = "Validates credentials and returns an auth token when login succeeds.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
            @ApiResponse(responseCode = "400", description = "Invalid username or password"),
            @ApiResponse(responseCode = "409", description = "User is already logged in"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserLoginDTO user) {
        return ResponseEntity.ok(authService.login(user));
    }

    @Operation(summary = "Logout current user", description = "Invalidates authentication tokens for the currently authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Logout completed successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials"),
            @ApiResponse(responseCode = "404", description = "Authenticated user was not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        authService.logout(username);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Change user password", description = "Changes the password of the currently authenticated user after verifying the old password and matching new password fields.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "The old password is incorrect or the new passwords do not match"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials"),
            @ApiResponse(responseCode = "404", description = "Authenticated user was not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        authService.changePassword(username, changePasswordDTO);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Request a forgot-password email", description = "Sends a password recovery email to the registered address for the current user.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Password reset email sent successfully"),
            @ApiResponse(responseCode = "400", description = "The provided email does not match the current user's email"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials"),
            @ApiResponse(responseCode = "404", description = "Authenticated user was not found"),
            @ApiResponse(responseCode = "409", description = "A valid forgot-password token already exists for the user"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PostMapping("/forgotPassword")
    public ResponseEntity<Void> sendForgotPasswordEmail(@Valid @RequestBody SendForgotPasswordEmailDTO dto) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String forgotPasswordToken = authService.sendForgotPasswordEmail(username, dto);

        Optional<String> shouldSkipEmailSending = featureFlagService.getFeatureFlagValueUnsafe("SKIP_EMAIL");
        HttpHeaders headers = new HttpHeaders();
        if (shouldSkipEmailSending.isPresent() && Boolean.parseBoolean(shouldSkipEmailSending.get())) {
            headers.add("Reset-Token", forgotPasswordToken);
        }
        return ResponseEntity
                .noContent()
                .headers(headers)
                .build();
    }

    @Operation(summary = "Reset forgotten password", description = "Resets the password for the current user using a valid forgot-password token.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "The new passwords do not match"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials"),
            @ApiResponse(responseCode = "404", description = "Authenticated user was not found"),
            @ApiResponse(responseCode = "409", description = "The password reset token is invalid or has already been used"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PatchMapping("/forgotPassword")
    public ResponseEntity<Void> changeForgottenPassword(@Valid @RequestBody ChangeForgottenPasswordDTO dto) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        authService.changeForgottenPassword(username, dto);
        return ResponseEntity.noContent().build();
    }
}
