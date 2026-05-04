package bg.sofia.uni.fmi.issuetracker.dto.ticket;

import bg.sofia.uni.fmi.issuetracker.model.ticket.Ticket;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketPriority;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketStatus;

import java.time.LocalDateTime;
import java.util.List;

public class TicketResponse {

    private String uuid;
    private String title;
    private String description;
    private TicketStatus ticketStatus;
    private TicketPriority ticketPriority;
    private String sprintUuid;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private LocalDateTime dueDate;
    private String projectUuid;
    private String assigneeUsername;
    private List<String> dependentTicketUuids;

    public static TicketResponse from(Ticket ticket) {

        TicketResponse response = new TicketResponse();

        response.uuid = ticket.getUuid();
        response.title = ticket.getTitle();
        response.description = ticket.getDescription();
        response.ticketStatus = ticket.getTicketStatus();
        response.ticketPriority = ticket.getTicketPriority();
        response.sprintUuid = ticket.getSprint().getUuid();
        response.createDate = ticket.getCreateDate();
        response.updateDate = ticket.getUpdateDate();
        response.dueDate = ticket.getDueDate();
        response.projectUuid = ticket.getProject().getUuid();
        response.assigneeUsername = ticket.getAssignee().getUsername();
        response.dependentTicketUuids = ticket.getDependentTickets().stream()
            .map(Ticket::getUuid)
            .toList();

        return response;
    }

    public String getUuid() {
        return uuid;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TicketStatus getTicketStatus() {
        return ticketStatus;
    }

    public TicketPriority getTicketPriority() {
        return ticketPriority;
    }

    public String getSprintUuid() {
        return sprintUuid;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public String getProjectUuid() {
        return projectUuid;
    }

    public String getAssigneeUsername() {
        return assigneeUsername;
    }

    public List<String> getDependentTicketUuids() {
        return dependentTicketUuids;
    }
}
