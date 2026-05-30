package bg.sofia.uni.fmi.issuetracker.dto.output.ticket;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response body containing basic information about a dependent ticket.")
public record DependentTicketDTO(
        @Schema(description = "Unique identifier code of the dependent ticket.", requiredMode = Schema.RequiredMode.REQUIRED)
        String code,
        @Schema(description = "Title of the dependent ticket.", requiredMode = Schema.RequiredMode.REQUIRED)
        String title,
        @Schema(description = "Description of the dependent ticket.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String description,
        @Schema(description = "Current status of the dependent ticket.", requiredMode = Schema.RequiredMode.REQUIRED)
        String status) {
}
