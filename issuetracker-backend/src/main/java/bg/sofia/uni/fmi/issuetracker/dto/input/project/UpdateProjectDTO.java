package bg.sofia.uni.fmi.issuetracker.dto.input.project;

import bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Request body for updating an existing project.")
public record UpdateProjectDTO(
    @Size(max = 500, message = ValidationConstants.Project.LENGTH_NAME)
    @Schema(description = "Updated name of the project. Optional, max 500 characters.",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 500)
    String name
) {
}
