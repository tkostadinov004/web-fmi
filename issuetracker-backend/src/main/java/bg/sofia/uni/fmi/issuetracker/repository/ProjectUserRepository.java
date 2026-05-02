package bg.sofia.uni.fmi.issuetracker.repository;

import bg.sofia.uni.fmi.issuetracker.model.project.ProjectUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, ProjectUser.ProjectUserKey> {
}
