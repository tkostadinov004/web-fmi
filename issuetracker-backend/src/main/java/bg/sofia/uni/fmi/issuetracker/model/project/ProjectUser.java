package bg.sofia.uni.fmi.issuetracker.model.project;

import bg.sofia.uni.fmi.issuetracker.model.auth.Role;
import bg.sofia.uni.fmi.issuetracker.model.auth.User;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "project_users")
public class ProjectUser {
    @Embeddable
    public static class ProjectUserKey {
        @Column(name = "project_uuid")
        private String projectUuid;

        @Column(name = "user_username")
        private String userUsername;

        @Column(name = "role")
        @Enumerated(value = EnumType.STRING)
        private Role role;

        public ProjectUserKey() {

        }

        public ProjectUserKey(String projectUuid, String userUsername, Role role) {
            this.projectUuid = projectUuid;
            this.userUsername = userUsername;
            this.role = role;
        }

        public String getProjectUuid() {
            return projectUuid;
        }

        public String getUserUsername() {
            return userUsername;
        }

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            ProjectUserKey that = (ProjectUserKey) o;
            return Objects.equals(projectUuid, that.projectUuid) && Objects.equals(userUsername, that.userUsername) &&
                role == that.role;
        }

        @Override
        public int hashCode() {
            return Objects.hash(projectUuid, userUsername, role);
        }
    }

    @EmbeddedId
    private ProjectUserKey id;

    @ManyToOne
    @MapsId("projectUuid")
    @JoinColumn(name = "project_uuid")
    private Project project;

    @ManyToOne
    @MapsId("userUsername")
    @JoinColumn(name = "user_username")
    private User user;

    public ProjectUser() {
    }

    public ProjectUser(Project project, User user, Role role) {
        this.project = project;
        this.user = user;
        this.id = new ProjectUserKey(project.getUuid(), user.getUsername(), role);
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProjectUser that = (ProjectUser) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
