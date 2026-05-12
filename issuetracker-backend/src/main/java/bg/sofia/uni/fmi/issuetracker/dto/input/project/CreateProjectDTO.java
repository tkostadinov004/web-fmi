package bg.sofia.uni.fmi.issuetracker.dto.input.project;

import bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request body for creating a new project.")
public record CreateProjectDTO(
    @NotBlank(message = ValidationConstants.Project.BLANK_NAME)
    @Size(max = 500, message = ValidationConstants.Project.LENGTH_NAME)
    @Schema(description = "Name of the project. Must not be blank and must be at most 500 characters.",
        requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 500, minLength = 1)
    String name
) {
}
