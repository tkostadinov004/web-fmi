package bg.sofia.uni.fmi.issuetracker.model.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
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

    public Project() {
    }

    public Project(String name) {
        this.name = name;
    }

    public Project(String name, Set<ProjectUser> users) {
        this.name = name;
        this.users = users;
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
}
