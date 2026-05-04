package bg.sofia.uni.fmi.issuetracker.model;

import bg.sofia.uni.fmi.issuetracker.model.project.ProjectUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    public static class UserBuilder {
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private String companyName;
        private String profilePicturePath;
        private String password;
        private boolean isAdmin;

        private UserBuilder() {
            isAdmin = false;
        }

        public static UserBuilder newBuilder() {
            return new UserBuilder();
        }

        public UserBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserBuilder companyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        public UserBuilder profilePicturePath(String profilePicturePath) {
            this.profilePicturePath = profilePicturePath;
            return this;
        }

        public UserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder admin(boolean admin) {
            this.isAdmin = admin;
            return this;
        }

        public User build() {
            return new User(username, email, firstName, lastName, companyName, profilePicturePath, password, isAdmin);
        }
    }

    @Id
    @Column(name = "username", length = 100)
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "first_name", length = 200)
    private String firstName;

    @Column(name = "last_name", length = 200)
    private String lastName;

    @Column(name = "company_name", length = 200)
    private String companyName;

    @Column(name = "profile_picture_path", length = 1024)
    private String profilePicturePath;

    @Column(name = "password")
    private String password;

    @Column(name = "admin")
    private boolean isAdmin;

    @Column(name = "deleted")
    private boolean isDeleted;

    @OneToMany(mappedBy = "user")
    private Set<ProjectUser> projects = new HashSet<>();

    public User() {
    }

    private User(String username, String email, String firstName, String lastName, String companyName, String profilePicturePath, String password, boolean isAdmin) {
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.companyName = companyName;
        this.profilePicturePath = profilePicturePath;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Set<ProjectUser> getProjects() {
        return projects;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getProfilePicturePath() {
        return profilePicturePath;
    }

    public void setProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
    }
}
