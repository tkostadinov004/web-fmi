package bg.sofia.uni.fmi.issuetracker.model.ticket;

import bg.sofia.uni.fmi.issuetracker.model.User;
import bg.sofia.uni.fmi.issuetracker.model.project.Project;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @Column(name = "code", nullable = false, unique = true, length = 100)
    private String code;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "ticket_status")
    @Enumerated(value = EnumType.STRING)
    private TicketStatus ticketStatus;

    @Column(name = "ticket_priority", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private TicketPriority ticketPriority;

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

    @ManyToMany
    @JoinTable(
            name = "ticket_dependencies",
            joinColumns = @JoinColumn(name = "ticket_code"),
            inverseJoinColumns = @JoinColumn(name = "depends_on_ticket_code")
    )
    private List<Ticket> dependentTickets;

    @OneToMany(mappedBy = "ticket")
    private List<TicketComment> ticketComments;

    public Ticket() {

    }

    public Ticket(String code, String title, String description, TicketPriority ticketPriority,
                  TicketStatus status, LocalDateTime dueDate, Project project, User assignee,
                  List<Ticket> dependentTickets) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.ticketPriority = ticketPriority;
        this.ticketStatus = status;
        this.createDate = LocalDateTime.now();
        this.updateDate = LocalDateTime.now();
        this.dueDate = dueDate;
        this.project = project;
        this.assignee = assignee;
        this.dependentTickets = dependentTickets;
    }

    public String getCode() {
        return code;
    }

    public Ticket setCode(String code) {
        this.code = code;
        return this;
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

    public List<TicketComment> getTicketComments() {
        return ticketComments;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Ticket ticket)) return false;
        return Objects.equals(code, ticket.code);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(code);
    }
}
