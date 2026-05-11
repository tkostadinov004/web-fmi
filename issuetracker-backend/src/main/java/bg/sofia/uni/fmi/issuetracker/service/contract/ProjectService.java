package bg.sofia.uni.fmi.issuetracker.service.contract;

import bg.sofia.uni.fmi.issuetracker.dto.input.project.CreateProjectDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.project.UpdateProjectDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.project.ProjectDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectNotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserNotFoundException;
import bg.sofia.uni.fmi.issuetracker.model.auth.Role;

import java.util.Collection;
import java.util.List;

public interface ProjectService {
    /**
     * Checks whether the given user is a member of the specified project.
     *
     * @param username  the username of the user
     * @param projectId the ID of the project
     * @return {@code true} if the user is a member of the project, {@code false} otherwise
     * @throws UserNotFoundException    if the user cannot be found
     * @throws ProjectNotFoundException if the project cannot be found
     */
    boolean isMemberOf(String username, String projectId);

    /**
     * Checks whether the user has the requested roles in the specified project.
     *
     * <p>If {@code strict} is {@code true}, the user must have every role in the provided collection.
     * If {@code strict} is {@code false}, the user must have at least one of the provided roles.</p>
     *
     * @param username  the username of the user
     * @param projectId the ID of the project
     * @param roles     the collection of roles to validate
     * @param strict    whether all provided roles are required ({@code true}) or any one role is sufficient ({@code false})
     * @return {@code true} if the user satisfies the role requirement, {@code false} otherwise
     * @throws UserNotFoundException    if the user cannot be found
     * @throws ProjectNotFoundException if the project cannot be found
     */
    boolean hasRoles(String username, String projectId, Collection<Role> roles, boolean strict);

    /**
     * Returns all projects
     *
     * @return {@link List<ProjectDetailsDTO>}
     */
    List<ProjectDetailsDTO> getAllProjects();

    /**
     * Returns a project by its id
     *
     * @param projectId the id of the project
     * @return {@link ProjectDetailsDTO}
     * @throws ProjectNotFoundException if the project cannot be found
     */
    ProjectDetailsDTO findProjectById(String projectId);

    /**
     * Returns a new project
     *
     * @param dto the {@link CreateProjectDTO} containing project creation data
     * @return the created {@link ProjectDetailsDTO} entity
     * @throws ProjectAlreadyExistsException if the project already exists
     */
    ProjectDetailsDTO addProject(CreateProjectDTO dto);

    /**
     * Updates project information
     *
     * @param projectId the id of the project
     * @param dto       the {@link UpdateProjectDTO} containing updated project data
     * @throws ProjectNotFoundException if the project cannot be found
     */
    void updateProject(String projectId, UpdateProjectDTO dto);
}
