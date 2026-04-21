package bg.sofia.uni.fmi.issuetracker.repository;

import bg.sofia.uni.fmi.issuetracker.model.auth.Project;
import bg.sofia.uni.fmi.issuetracker.model.auth.Role;
import bg.sofia.uni.fmi.issuetracker.model.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository extends JpaRepository<Project, String> {
    @Query("select count(pu.id) > 0 from ProjectUser pu where pu.project = :project and pu.user = :user")
    boolean isUserInProject(@Param("user") User user, @Param("project") Project project);

    @Query("select count(pu.id) > 0 from ProjectUser pu where pu.project = :project and pu.user = :user and pu.id.role = :role")
    boolean hasRole(@Param("user") User user, @Param("project") Project project, @Param("role") Role role);
}
