package bg.sofia.uni.fmi.issuetracker.dto.input.ticket;

import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketPriority;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Schema(description = "Request body for creating a new ticket.")
public record CreateTicketDTO(@NotBlank(message = ValidationConstants.Ticket.BLANK_CODE)
                              @Size(max = 100, message = ValidationConstants.Ticket.LENGTH_CODE)
                              @Schema(description = "Unique identifier code for the ticket. Must not be blank and must be at most 100 characters.", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100, minLength = 1)
                              String code,
                              @NotBlank(message = ValidationConstants.Ticket.BLANK_TITLE)
                              @Size(max = 100, message = ValidationConstants.Ticket.LENGTH_TITLE)
                              @Schema(description = "Title of the ticket. Must not be blank and must be at most 100 characters.", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100, minLength = 1)
                              String title,
                              @Size(max = 500, message = ValidationConstants.Ticket.LENGTH_DESCRIPTION)
                              @Schema(description = "Detailed description of the ticket. Optional, maximum 500 characters.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 500)
                              String description,
                              @NotNull(message = ValidationConstants.Ticket.NULL_PRIORITY)
                              @Schema(description = "Priority level of the ticket. Required.", requiredMode = Schema.RequiredMode.REQUIRED)
                              TicketPriority ticketPriority,
                              @Future(message = ValidationConstants.Ticket.DUE_DATE_IN_THE_PAST)
                              @Schema(description = "Due date for the ticket in the future. Optional. Format: YYYY-MM-DD HH:mm.", example = "2026-12-31 14:30", requiredMode = Schema.RequiredMode.NOT_REQUIRED, type = "string")
                              @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
                              LocalDateTime dueDate,
                              @Schema(description = "Username of the user assigned to this ticket. Optional.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
                              String assigneeUsername) {

}
