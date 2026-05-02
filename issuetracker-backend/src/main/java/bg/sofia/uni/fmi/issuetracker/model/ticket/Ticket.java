package bg.sofia.uni.fmi.issuetracker.model.ticket;

import bg.sofia.uni.fmi.issuetracker.model.auth.User;
import bg.sofia.uni.fmi.issuetracker.model.project.Project;
import bg.sofia.uni.fmi.issuetracker.model.sprint.Sprint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid")
    private String uuid;

    // Human readable identification
//    @Column(name = "code", nullable = false, unique = true, length = 100)
//    private String code;

    @Column(name = "title", nullable = false, unique = true, length = 100)
    private String title;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "ticket_status")
    @Enumerated(value = EnumType.STRING)
    private TicketStatus ticketStatus;

    @Column(name = "ticket_priority", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private TicketPriority ticketPriority;

    @ManyToOne
    @JoinColumn(name = "sprint_uuid")
    private Sprint sprint;

    @Column(name = "create_date")
    private LocalDateTime createDate = LocalDateTime.now();

    @Column(name = "update_date")
    private LocalDateTime updateDate = LocalDateTime.now();

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @ManyToOne
    @JoinColumn(name = "project_uuid", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "assignee_username")
    private User assignee;

    // Should there be List of tickets 'this' depends on ( not only tickets that depend on 'this' ) ???
    // Is this ok ???
    @ManyToMany
    @JoinTable(
        name = "ticket_dependencies",
        joinColumns = @JoinColumn(name = "ticket_uuid"),
        inverseJoinColumns = @JoinColumn(name = "depends_on_ticket_uuid")
    )
    private List<Ticket> dependentTickets;

    public Ticket() {

    }

    public Ticket(String title, String description, Sprint sprint, TicketPriority ticketPriority,
                  TicketStatus status, LocalDateTime dueDate, Project project, User assignee,
                  List<Ticket> dependentTickets) {
        this.title = title;
        this.description = description;
        this.sprint = sprint;
        this.ticketPriority = ticketPriority;
        this.ticketStatus = status;
        this.createDate = LocalDateTime.now();
        this.updateDate = LocalDateTime.now();
        this.dueDate = dueDate;
        this.project = project;
        this.assignee = assignee;
        this.dependentTickets = dependentTickets;
    }

    public String getUuid() {
        return uuid;
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

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate() {
        this.updateDate = LocalDateTime.now();
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

    public List<Ticket> getDependentTickets() {
        return dependentTickets;
    }

    public void setDependentTickets(List<Ticket> dependentTickets) {
        this.dependentTickets = dependentTickets;
    }

    public void addDependentTicket(Ticket ticket) {
        this.dependentTickets.add(ticket);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Ticket ticket)) return false;
        return Objects.equals(uuid, ticket.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }
}
