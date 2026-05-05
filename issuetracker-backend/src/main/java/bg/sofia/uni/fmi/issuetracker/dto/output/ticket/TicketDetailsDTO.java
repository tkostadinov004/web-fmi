package bg.sofia.uni.fmi.issuetracker.dto.output.ticket;

import bg.sofia.uni.fmi.issuetracker.model.ticket.Ticket;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketPriority;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Response body containing detailed information about a ticket.")
public record TicketDetailsDTO(
        @Schema(description = "Unique identifier code of the ticket.", requiredMode = Schema.RequiredMode.REQUIRED)
        String code,
        @Schema(description = "Title of the ticket.", requiredMode = Schema.RequiredMode.REQUIRED)
        String title,
        @Schema(description = "Detailed description of the ticket.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String description,
        @Schema(description = "Current status of the ticket (e.g., OPEN, IN_PROGRESS, CLOSED).", requiredMode = Schema.RequiredMode.REQUIRED)
        TicketStatus ticketStatus,
        @Schema(description = "Priority level of the ticket (e.g., LOW, MEDIUM, HIGH, CRITICAL).", requiredMode = Schema.RequiredMode.REQUIRED)
        TicketPriority ticketPriority,
        @Schema(description = "UUID of the sprint associated with the ticket.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String sprintUuid,
        @Schema(description = "Timestamp when the ticket was created. Format: ISO 8601 (YYYY-MM-DDTHH:mm:ss).", requiredMode = Schema.RequiredMode.REQUIRED, type = "string", format = "date-time")
        LocalDateTime createDate,
        @Schema(description = "Timestamp when the ticket was last updated. Format: ISO 8601 (YYYY-MM-DDTHH:mm:ss).", requiredMode = Schema.RequiredMode.REQUIRED, type = "string", format = "date-time")
        LocalDateTime updateDate,
        @Schema(description = "Due date for the ticket. Format: ISO 8601 (YYYY-MM-DDTHH:mm:ss).", requiredMode = Schema.RequiredMode.NOT_REQUIRED, type = "string", format = "date-time")
        LocalDateTime dueDate,
        @Schema(description = "UUID of the project this ticket belongs to.", requiredMode = Schema.RequiredMode.REQUIRED)
        String projectUuid,
        @Schema(description = "Information about the user assigned to this ticket.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        TicketDetailsAssigneeDTO assignee,
        @Schema(description = "List of tickets that depend on this ticket.", requiredMode = Schema.RequiredMode.REQUIRED)
        List<DependentTicketDTO> dependentTickets) {
    public static TicketDetailsDTO from(Ticket ticket) {
        TicketDetailsAssigneeDTO assigneeDTO = new TicketDetailsAssigneeDTO(ticket.getAssignee().getProfilePicturePath(), ticket.getAssignee().getUsername());
        List<DependentTicketDTO> dependentTicketDTOS = ticket
                .getDependentTickets()
                .stream()
                .map(dt -> new DependentTicketDTO(dt.getCode(), dt.getTitle(), dt.getDescription(), dt.getTicketStatus()))
                .toList();

        return new TicketDetailsDTO(ticket.getCode(), ticket.getTitle(), ticket.getDescription(), ticket.getTicketStatus(),
                ticket.getTicketPriority(), ticket.getSprint().getUuid(), ticket.getCreateDate(), ticket.getUpdateDate(),
                ticket.getDueDate(), ticket.getProject().getUuid(), assigneeDTO, dependentTicketDTOS);
    }
}
