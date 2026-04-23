package bg.sofia.uni.fmi.issuetracker.repository;

import bg.sofia.uni.fmi.issuetracker.model.auth.Project;
import bg.sofia.uni.fmi.issuetracker.model.auth.Role;
import bg.sofia.uni.fmi.issuetracker.model.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface ProjectRepository extends JpaRepository<Project, String> {
    @Query("select count(pu.id) > 0 from ProjectUser pu where pu.project = :project and pu.user = :user")
    boolean isUserInProject(@Param("user") User user, @Param("project") Project project);

    @Query("select count(pu.id) > 0 " +
            "from ProjectUser pu " +
            "where pu.project = :project and pu.user = :user and pu.id.role in :roles")
    boolean hasRoles(@Param("user") User user, @Param("project") Project project, @Param("roles") Iterable<Role> roles);

    @Query("select count(pu.id) = :#{roles.size()} " +
            "from ProjectUser pu " +
            "where pu.project = :project and pu.user = :user and pu.id.role in :roles")
    boolean hasRolesStrict(@Param("user") User user, @Param("project") Project project, @Param("roles") Collection<Role> roles);
}
