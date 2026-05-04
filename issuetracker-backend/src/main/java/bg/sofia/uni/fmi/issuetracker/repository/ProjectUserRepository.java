package bg.sofia.uni.fmi.issuetracker.repository;

import bg.sofia.uni.fmi.issuetracker.model.project.ProjectUser;
import bg.sofia.uni.fmi.issuetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, ProjectUser.ProjectUserKey> {
    List<ProjectUser> findAllByUser(User user);
}
