package bg.sofia.uni.fmi.issuetracker.service.contract;

import bg.sofia.uni.fmi.issuetracker.dto.input.project.CreateProjectDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.project.CreateProjectUserDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.project.UpdateProjectDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.project.ProjectDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.project.ProjectDetailsUserDTO;
import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectNotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectUserAlreadyInProjectException;
import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectUserNotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserNotFoundException;
import bg.sofia.uni.fmi.issuetracker.model.auth.Role;

import java.util.Collection;
import java.util.List;

public interface ProjectService {
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
     * Returns all users assigned to a project
     *
     * @param projectId the id of the project
     * @return {@link List< ProjectDetailsUserDTO >}
     * @throws ProjectNotFoundException if the project cannot be found
     */
    List<ProjectDetailsUserDTO> getProjectUsers(String projectId);

    /**
     * Returns the newly assigned user of the project
     *
     * @param projectId the id of the project
     * @param dto       the {@link CreateProjectUserDTO} containing project user creation data
     * @return {@link ProjectDetailsUserDTO}
     * @throws ProjectNotFoundException             if the project cannot be found
     * @throws UserNotFoundException                if the user cannot be found
     * @throws ProjectUserAlreadyInProjectException if the user is already in the project
     */
    ProjectDetailsUserDTO addProjectUser(String projectId, CreateProjectUserDTO dto, String addInitiatorUsername);

    /**
     * Delete a project user
     *
     * @param projectId the id of the project
     * @param username  the username of the project user
     * @throws ProjectNotFoundException     if the project cannot be found
     * @throws UserNotFoundException        if the user cannot be found
     * @throws ProjectUserNotFoundException if the project user is not found
     */
    void deleteProjectUser(String projectId, String username);

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
     * Creates a new project
     *
     * @param dto      the {@link CreateProjectDTO} containing project creation data
     * @param username the username of the project creator
     * @throws ProjectAlreadyExistsException if the project already exists
     * @throws UserNotFoundException         if such a user does not exist
     */
    void addProject(CreateProjectDTO dto, String username);

    /**
     * Updates project information
     *
     * @param projectId the id of the project
     * @param dto       the {@link UpdateProjectDTO} containing updated project data
     * @throws ProjectNotFoundException if the project cannot be found
     */
    void updateProject(String projectId, UpdateProjectDTO dto);

    /**
     * Delete a project
     *
     * @param projectId the id of the project to delete
     * @throws ProjectNotFoundException if the project cannot be found
     */
    void deleteProject(String projectId);
}
