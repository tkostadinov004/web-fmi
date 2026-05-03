package bg.sofia.uni.fmi.issuetracker.dto.input.auth;

import bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request body for initiating forgot-password email delivery.")
public record SendForgotPasswordEmailDTO(
        @NotBlank(message = ValidationConstants.Auth.BLANK_EMAIL)
        @Email(message = ValidationConstants.Auth.INVALID_EMAIL)
        @Schema(description = "Email address associated with the user account. Must not be blank and must be a valid email.", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1, format = "email")
        String email,

        @NotBlank(message = ValidationConstants.Auth.BLANK_REDIRECT_URL)
        @Schema(description = "Redirect URL included in the reset email. Must not be blank.", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1)
        String redirectUrl) {
}
