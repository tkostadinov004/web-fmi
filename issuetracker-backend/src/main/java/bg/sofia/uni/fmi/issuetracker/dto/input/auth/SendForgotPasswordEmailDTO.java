package bg.sofia.uni.fmi.issuetracker.dto.input.auth;

import bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendForgotPasswordEmailDTO(
        @NotBlank(message = ValidationConstants.Auth.BLANK_EMAIL) @Email(message = ValidationConstants.Auth.INVALID_EMAIL) String email,
        @NotBlank(message = ValidationConstants.Auth.BLANK_REDIRECT_URL) String redirectUrl) {
}
