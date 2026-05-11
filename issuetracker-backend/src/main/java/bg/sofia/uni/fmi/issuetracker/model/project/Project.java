package bg.sofia.uni.fmi.issuetracker.model.project;

import bg.sofia.uni.fmi.issuetracker.model.User;
import bg.sofia.uni.fmi.issuetracker.model.ticket.Ticket;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid")
    private String uuid;

    @Column(name = "name", nullable = false, length = 500)
    private String name;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "project")
    private Set<ProjectUser> users = new HashSet<>();

    // Is this correct ???
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();

    public Project() {
    }

    public Project(String name, String description, User createdBy,  Set<ProjectUser> users,
                   List<Ticket> tickets) {
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
        this.users = users;
        this.tickets = tickets == null ? new ArrayList<>() : tickets;
    }

    public Project(String name) {
        this.name = name;
    }

    public Project(String name, Set<ProjectUser> users, List<Ticket> tickets) {
        this.name = name;
        this.users = users;
        this.tickets = tickets;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Set<ProjectUser> getUsers() {
        return users;
    }

    public void setUsers(Set<ProjectUser> users) {
        this.users = users;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public void addTicket(Ticket ticket) {
        this.tickets.add(ticket);
    }

    public void removeTicket(Ticket ticket) {
        this.tickets.remove(ticket);
    }

    public void addProjectUser(ProjectUser user) {
        users.add(user);
    }

    public void removeProjectUser(ProjectUser user) {
        users.remove(user);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Project project)) return false;
        return Objects.equals(uuid, project.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }
}
