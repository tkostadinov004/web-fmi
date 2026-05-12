package bg.sofia.uni.fmi.issuetracker.service;

import bg.sofia.uni.fmi.issuetracker.dto.input.project.CreateProjectDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.project.CreateProjectUserDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.project.UpdateProjectDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.project.ProjectDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.project.ProjectDetailsUserDTO;
import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectNotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectUserNotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.project.UserNotPartOfProjectException;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserNotFoundException;
import bg.sofia.uni.fmi.issuetracker.model.User;
import bg.sofia.uni.fmi.issuetracker.model.auth.Role;
import bg.sofia.uni.fmi.issuetracker.model.project.Project;
import bg.sofia.uni.fmi.issuetracker.model.project.ProjectUser;
import bg.sofia.uni.fmi.issuetracker.repository.ProjectRepository;
import bg.sofia.uni.fmi.issuetracker.repository.ProjectUserRepository;
import bg.sofia.uni.fmi.issuetracker.repository.UserRepository;
import bg.sofia.uni.fmi.issuetracker.service.contract.ProjectService;
import bg.sofia.uni.fmi.issuetracker.service.mapper.ProjectMapper;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service("projectService")
public class ProjectServiceImpl implements ProjectService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectUserRepository projectUserRepository;
    private final ProjectMapper projectMapper;

    public ProjectServiceImpl(UserRepository userRepository, ProjectRepository projectRepository,
                              ProjectUserRepository projectUserRepository, ProjectMapper projectMapper) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.projectUserRepository = projectUserRepository;
        this.projectMapper = projectMapper;
    }

    @Override
    public boolean isMemberOf(String username, String projectId) {
        User user = getUser(username);
        Project project = getProject(projectId);

        return projectRepository.isUserInProject(user, project);
    }

    @Override
    public boolean hasRoles(String username, String projectId, Collection<Role> roles, boolean strict) {
        User user = getUser(username);
        Project project = getProject(projectId);

        return strict ? projectRepository.hasRolesStrict(user, project, roles) :
                projectRepository.hasRoles(user, project, roles);
    }

    @Override
    public List<ProjectDetailsUserDTO> getProjectUsers(String projectId) {
        Project project = getProject(projectId);

        return projectUserRepository.findAllByProject(project).stream()
                .map(projectUser -> new ProjectDetailsUserDTO(projectUser.getUser().getProfilePicturePath(),
                        projectUser.getUser().getUsername()))
                .toList();
    }

    @Override
    public ProjectDetailsUserDTO addProjectUser(String projectId, CreateProjectUserDTO dto,
                                                Role role) {
        Project project = getProject(projectId);
        User userToAdd = getUser(dto.username());

        if (projectRepository.isUserInProject(userToAdd, project)) {
            throw new UserNotPartOfProjectException(
                    ExceptionMessages.ProjectUser.userAlreadyInProject(userToAdd.getUsername(), projectId));
        }

        ProjectUser projectUser = projectUserRepository.save(new ProjectUser(project, userToAdd, role));

        return new ProjectDetailsUserDTO(userToAdd.getProfilePicturePath(), projectUser.getUser().getUsername());
    }

    @Override
    public void deleteProjectUser(String projectId, String username) {
        ProjectUser projectUser = getProjectUser(projectId, username);

        projectUserRepository.delete(projectUser);
    }

    @Override
    public List<ProjectDetailsDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(ProjectDetailsDTO::from)
                .toList();
    }

    @Override
    public ProjectDetailsDTO findProjectById(String projectId) {
        return ProjectDetailsDTO.from(getProject(projectId));
    }

    @Override
    public void addProject(CreateProjectDTO dto) {
        if (projectRepository.existsByName(dto.name())) {
            throw new ProjectAlreadyExistsException(ExceptionMessages.Project.projectAlreadyExists(dto.name())
            );
        }

        Project project = new Project(dto.name());

        projectRepository.save(project);
    }

    @Override
    public void updateProject(String projectId, UpdateProjectDTO dto) {
        Project project = getProject(projectId);

        projectMapper.patchProjectFromDTO(dto, project);
        projectRepository.save(project);
    }

    @Override
    public void deleteProject(String projectId) {
        Project project = getProject(projectId);

        projectRepository.delete(project);
    }

    private User getUser(String username) {
        Optional<User> user = userRepository.findById(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException(ExceptionMessages.User.userNotFound(username));
        }
        return user.get();
    }

    private Project getProject(String projectId) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isEmpty()) {
            throw new ProjectNotFoundException(ExceptionMessages.Project.projectNotFound(projectId));
        }
        return project.get();
    }

    private ProjectUser getProjectUser(String projectId, String username) {
        Project project = getProject(projectId);
        User userToDelete = getUser(username);

        Optional<ProjectUser> projectUser = projectUserRepository.findByProjectAndUser(project, userToDelete);
        if (projectUser.isEmpty()) {
            throw new ProjectUserNotFoundException(ExceptionMessages.ProjectUser.userNotFound(username, projectId));
        }

        return projectUser.get();
    }
}