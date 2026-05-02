package bg.sofia.uni.fmi.issuetracker.dto.input.auth;

import bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants;
import jakarta.validation.constraints.NotBlank;

public record ChangeForgottenPasswordDTO(
        @NotBlank(message = ValidationConstants.Auth.BLANK_PASSWORD) String newPassword,
        @NotBlank(message = ValidationConstants.Auth.BLANK_PASSWORD) String repeatedNewPassword,
        String token) {
}
