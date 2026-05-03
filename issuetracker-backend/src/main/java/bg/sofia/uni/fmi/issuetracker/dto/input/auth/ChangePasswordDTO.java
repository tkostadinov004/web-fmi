package bg.sofia.uni.fmi.issuetracker.dto.input.auth;

import bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request body for changing the current user's password.")
public record ChangePasswordDTO(
        @NotBlank(message = ValidationConstants.Auth.BLANK_PASSWORD)
        @Schema(description = "Current password. Must not be blank.", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1)
        String oldPassword,

        @NotBlank(message = ValidationConstants.Auth.BLANK_PASSWORD)
        @Schema(description = "New password. Must not be blank.", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1)
        String newPassword,

        @NotBlank(message = ValidationConstants.Auth.BLANK_PASSWORD)
        @Schema(description = "Repeated new password for confirmation. Must not be blank and must match newPassword.", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1)
        String repeatedNewPassword) {
}
