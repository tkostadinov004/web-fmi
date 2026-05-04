package bg.sofia.uni.fmi.issuetracker.model.project;

import bg.sofia.uni.fmi.issuetracker.model.ticket.Ticket;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid")
    private String uuid;

    @Column(name = "name", length = 500)
    private String name;

    @OneToMany(mappedBy = "project")
    private Set<ProjectUser> users = new HashSet<>();

    // Is this correct ???
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets;

    public Project() {
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
}
