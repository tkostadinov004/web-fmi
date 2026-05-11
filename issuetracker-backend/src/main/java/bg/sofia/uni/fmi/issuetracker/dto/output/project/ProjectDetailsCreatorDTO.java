package bg.sofia.uni.fmi.issuetracker.dto.output.project;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response body containing basic information about the project maker.")
public record ProjectDetailsCreatorDTO(
    @Schema(description = "Path or URL to the assignee's profile picture.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String profilePicturePath,
    @Schema(description = "Username of the creator.", requiredMode = Schema.RequiredMode.REQUIRED)
    String username,
    @Schema(description = "Email of the creator", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String email
) {
}
