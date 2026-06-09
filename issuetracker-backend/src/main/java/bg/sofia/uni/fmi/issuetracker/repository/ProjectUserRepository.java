package bg.sofia.uni.fmi.issuetracker.repository;

import bg.sofia.uni.fmi.issuetracker.model.User;
import bg.sofia.uni.fmi.issuetracker.model.project.Project;
import bg.sofia.uni.fmi.issuetracker.model.project.ProjectUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, ProjectUser.ProjectUserKey> {
    List<ProjectUser> findAllByProject(Project project);

    void deleteAllByProjectAndUser(Project project, User user);
}
