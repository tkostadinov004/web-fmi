package bg.sofia.uni.fmi.issuetracker.model.ticket;

import bg.sofia.uni.fmi.issuetracker.model.User;
import bg.sofia.uni.fmi.issuetracker.model.project.Project;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Embeddable
    public static class TicketKey {
        @Column(name = "code", nullable = false, unique = true, length = 100)
        private String code;

        @Column(name = "project_uuid", nullable = false)
        private String projectUuid;

        public TicketKey() {
        }

        public TicketKey(String code, String projectUuid) {
            this.code = code;
            this.projectUuid = projectUuid;
        }

        public String getCode() {
            return code;
        }

        public String getProjectUuid() {
            return projectUuid;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            TicketKey ticketKey = (TicketKey) o;
            return Objects.equals(code, ticketKey.code) && Objects.equals(projectUuid, ticketKey.projectUuid);
        }

        @Override
        public int hashCode() {
            return Objects.hash(code, projectUuid);
        }
    }

    @EmbeddedId
    private TicketKey id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "ticket_status")
    private String ticketStatus;

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
    @MapsId("projectUuid")
    @JoinColumn(name = "project_uuid", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "assignee_username")
    private User assignee;

    @ManyToMany
    @JoinTable(
            name = "ticket_dependencies",
            joinColumns = {@JoinColumn(name = "ticket_code"), @JoinColumn(name = "ticket_project_uuid")},
            inverseJoinColumns = {@JoinColumn(name = "depends_on_ticket_code"), @JoinColumn(name = "depends_on_ticket_project_uuid")}
    )
    private List<Ticket> dependentTickets = new ArrayList<>();

    @OneToMany(mappedBy = "ticket")
    private List<TicketComment> ticketComments = new ArrayList<>();

    public Ticket() {

    }

    public Ticket(String code, String title, String description, TicketPriority ticketPriority,
                  String status, LocalDateTime dueDate, Project project, User assignee,
                  List<Ticket> dependentTickets) {
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
        this.id = new TicketKey(code, project.getUuid());
    }

    public String getCode() {
        return id.code;
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

    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
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

    public void addTicketComment(TicketComment ticketComment) {
        this.ticketComments.add(ticketComment);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Ticket ticket)) return false;
        return Objects.equals(id, ticket.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
