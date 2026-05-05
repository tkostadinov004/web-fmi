package bg.sofia.uni.fmi.issuetracker.dto.output;

import bg.sofia.uni.fmi.issuetracker.model.auth.Role;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response body containing basic project information for a user.")
public record OutputUserProjectDTO(
        @Schema(description = "Name of the project.", requiredMode = Schema.RequiredMode.REQUIRED)
        String projectName,
        @Schema(description = "Unique identifier (UUID) of the project.", requiredMode = Schema.RequiredMode.REQUIRED)
        String projectId,
        @Schema(description = "User's role within the project (e.g., OWNER, DEVELOPER, VIEWER).", requiredMode = Schema.RequiredMode.REQUIRED)
        Role role) {
}
