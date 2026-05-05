package bg.sofia.uni.fmi.issuetracker.dto.input.ticket;

import bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request body for creating a new comment on a ticket.")
public record CreateTicketCommentDTO(
        @NotBlank(message = ValidationConstants.TicketComment.BLANK_CONTENT)
        @Schema(description = "Content of the comment. Must not be blank.", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1)
        String content) {
}
