package bg.sofia.uni.fmi.issuetracker.service;

import bg.sofia.uni.fmi.issuetracker.dto.input.project.CreateProjectDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.project.UpdateProjectDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.project.ProjectDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectNotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserNotFoundException;
import bg.sofia.uni.fmi.issuetracker.model.User;
import bg.sofia.uni.fmi.issuetracker.model.auth.Role;
import bg.sofia.uni.fmi.issuetracker.model.project.Project;
import bg.sofia.uni.fmi.issuetracker.repository.ProjectRepository;
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
    private final ProjectMapper projectMapper;

    public ProjectServiceImpl(UserRepository userRepository, ProjectRepository projectRepository,
                              ProjectMapper projectMapper) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
    }

    @Override
    public boolean isMemberOf(String username, String projectId) {
        User user = checkUser(username);
        Project project = checkProject(projectId);

        return projectRepository.isUserInProject(user, project);
    }

    @Override
    public boolean hasRoles(String username, String projectId, Collection<Role> roles, boolean strict) {
        User user = checkUser(username);
        Project project = checkProject(projectId);

        return strict ? projectRepository.hasRolesStrict(user, project, roles) :
            projectRepository.hasRoles(user, project, roles);
    }

    @Override
    public List<ProjectDetailsDTO> getAllProjects() {
        return projectRepository.findAll().stream()
            .map(ProjectDetailsDTO::from)
            .toList();
    }

    @Override
    public ProjectDetailsDTO findProjectById(String projectId) {
        return ProjectDetailsDTO.from(checkProject(projectId));
    }

    @Override
    public ProjectDetailsDTO addProject(CreateProjectDTO dto) {
        if (projectRepository.existsByName(dto.name())) {
            throw new ProjectAlreadyExistsException(ExceptionMessages.Project.projectAlreadyExists(dto.name())
            );
        }

        Project project = new Project(dto.name());

        return ProjectDetailsDTO.from(projectRepository.save(project));
    }

    @Override
    public void updateProject(String projectId, UpdateProjectDTO dto) {
        Project project = checkProject(projectId);

        projectMapper.patchProjectFromDTO(dto, project);
        projectRepository.save(project);
    }

    private User checkUser(String username) {
        Optional<User> user = userRepository.findById(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException(ExceptionMessages.User.userNotFound(username));
        }
        return user.get();
    }

    private Project checkProject(String projectId) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isEmpty()) {
            throw new ProjectNotFoundException(ExceptionMessages.Project.projectNotFound(projectId));
        }
        return project.get();
    }
}