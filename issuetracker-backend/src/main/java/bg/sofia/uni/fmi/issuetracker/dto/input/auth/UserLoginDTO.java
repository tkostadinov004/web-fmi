package bg.sofia.uni.fmi.issuetracker.dto.input.auth;

import bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request body for logging in a user.")
public record UserLoginDTO(
        @NotBlank(message = ValidationConstants.Auth.BLANK_USERNAME)
        @Schema(description = "Username of the user. Must not be blank.", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1)
        String username,

        @NotBlank(message = ValidationConstants.Auth.BLANK_PASSWORD)
        @Schema(description = "Password of the user. Must not be blank.", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1)
        String password) {
}

