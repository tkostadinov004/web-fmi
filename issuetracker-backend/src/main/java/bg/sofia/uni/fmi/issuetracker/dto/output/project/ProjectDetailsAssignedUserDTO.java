package bg.sofia.uni.fmi.issuetracker.dto.output.project;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response body containing basic information about an assigned user.")
public record ProjectDetailsAssignedUserDTO(
    @Schema(description = "Path or URL to the assignee's profile picture.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String profilePicturePath,
    @Schema(description = "Username of the assigned user.", requiredMode = Schema.RequiredMode.REQUIRED)
    String username
) {
}
