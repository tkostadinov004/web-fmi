package bg.sofia.uni.fmi.issuetracker.dto.input.ticket;

import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketPriority;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Schema(description = "Request body for updating an existing ticket.")
public record UpdateTicketDTO(@Size(max = 100, message = ValidationConstants.Ticket.LENGTH_TITLE)
                              @Schema(description = "Updated title of the ticket. Optional, maximum 100 characters.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 100)
                              String title,
                              @Size(max = 500, message = ValidationConstants.Ticket.LENGTH_DESCRIPTION)
                              @Schema(description = "Updated description of the ticket. Optional, maximum 500 characters.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 500)
                              String description,
                              @Schema(description = "Updated status of the ticket. Optional.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
                              String ticketStatus,
                              @Schema(description = "Updated priority level of the ticket. Optional.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
                              TicketPriority ticketPriority,
                              @Future(message = ValidationConstants.Ticket.DUE_DATE_IN_THE_PAST)
                              @Schema(description = "Due date for the ticket in the future. Optional. Format: YYYY-MM-DD HH:mm.", example = "2026-12-31 14:30", requiredMode = Schema.RequiredMode.NOT_REQUIRED, type = "string")
                              LocalDateTime dueDate) {
}
