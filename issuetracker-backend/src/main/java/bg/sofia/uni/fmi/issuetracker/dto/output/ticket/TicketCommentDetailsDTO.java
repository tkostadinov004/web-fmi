package bg.sofia.uni.fmi.issuetracker.dto.output.ticket;

import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketComment;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Response body containing detailed information about a ticket comment.")
public record TicketCommentDetailsDTO(
        @Schema(description = "Unique identifier (UUID) of the comment.", requiredMode = Schema.RequiredMode.REQUIRED)
        String uuid,
        @Schema(description = "Content of the comment.", requiredMode = Schema.RequiredMode.REQUIRED)
        String content,
        @Schema(description = "Timestamp when the comment was created. Format: ISO 8601 (YYYY-MM-DDTHH:mm:ss).", requiredMode = Schema.RequiredMode.REQUIRED, type = "string", format = "date-time")
        LocalDateTime createDate,
        @Schema(description = "Code of the ticket this comment belongs to.", requiredMode = Schema.RequiredMode.REQUIRED)
        String ticketCode,
        @Schema(description = "Username of the comment's author.", requiredMode = Schema.RequiredMode.REQUIRED)
        String authorUsername) {
    public static TicketCommentDetailsDTO from(TicketComment ticketComment) {
        return new TicketCommentDetailsDTO(ticketComment.getUuid(), ticketComment.getContent(),
                ticketComment.getCreatedAt(), ticketComment.getUuid(), ticketComment.getAuthor().getUsername());
    }
}
