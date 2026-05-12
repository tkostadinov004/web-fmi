package bg.sofia.uni.fmi.issuetracker.dto.input.project;

import bg.sofia.uni.fmi.issuetracker.model.auth.Role;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateProjectUserDTO(
        @NotBlank(message = ValidationConstants.User.NULL_VALUE)
        @NotNull(message = ValidationConstants.User.NULL_VALUE)
        @Size(max = 30, message = ValidationConstants.User.LENGTH_NAME)
        @Schema(description = "Name of the project. Must not be blank and must be at most 30 characters.",
                requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 30, minLength = 1)
        String username,
        @Parameter(description = "Role of the user in the project", required = true)
        Role role
) {
}
