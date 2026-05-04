package bg.sofia.uni.fmi.issuetracker.dto.ticket;

import bg.sofia.uni.fmi.issuetracker.model.project.Project;
import bg.sofia.uni.fmi.issuetracker.model.sprint.Sprint;
import bg.sofia.uni.fmi.issuetracker.model.ticket.Ticket;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketPriority;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public class TicketRequest {

    @NotNull
    @NotBlank
    private String uuid;

    @NotNull
    @NotBlank
    @Size(max = 100)
    private String title;

    @Size(max = 500)
    private String description;

    private TicketStatus ticketStatus;

    @NotNull
    private TicketPriority ticketPriority;

    private Sprint sprint;

    private LocalDateTime dueDate;

    @NotNull
    private Project project;

    private User assignee;

    private List<Ticket> dependentTicketUuids;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TicketStatus getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(TicketStatus ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public TicketPriority getTicketPriority() {
        return ticketPriority;
    }

    public void setTicketPriority(TicketPriority ticketPriority) {
        this.ticketPriority = ticketPriority;
    }

    public Sprint getSprint() {
        return sprint;
    }

    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getAssignee() {
        return assignee;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    public List<Ticket> getDependentTicketUuids() {
        return dependentTicketUuids;
    }

    public void setDependentTicketUuids(List<Ticket> dependentTicketUuids) {
        this.dependentTicketUuids = dependentTicketUuids;
    }
}
