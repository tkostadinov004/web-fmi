package bg.sofia.uni.fmi.issuetracker.dto.input.ticket;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request body for updating an existing comment on a ticket.")
public record UpdateTicketCommentDTO(
        @Schema(description = "Updated content of the comment. Optional.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String content) {
}
