package bg.sofia.uni.fmi.issuetracker.repository;

import bg.sofia.uni.fmi.issuetracker.model.User;
import bg.sofia.uni.fmi.issuetracker.model.project.Project;
import bg.sofia.uni.fmi.issuetracker.model.project.ProjectUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, ProjectUser.ProjectUserKey> {
    List<ProjectUser> findAllByUser(User user);

    List<ProjectUser> findAllByProject(Project project);

    Optional<ProjectUser> findByProjectAndUser(Project project, User user);

    void deleteAllByProjectAndUser(Project project, User user);
}
