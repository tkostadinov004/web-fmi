package bg.sofia.uni.fmi.issuetracker.dto.output.project;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response body containing basic information about an added ticket.")
public record ProjectDetailsAddedTicketDTO(
    @Schema(description = "Code of the ticket.", requiredMode = Schema.RequiredMode.REQUIRED)
    String code,
    @Schema(description = "Title of the ticket.", requiredMode = Schema.RequiredMode.REQUIRED)
    String title
) {
}
