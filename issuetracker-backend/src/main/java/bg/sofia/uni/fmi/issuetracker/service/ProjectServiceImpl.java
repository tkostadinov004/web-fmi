package bg.sofia.uni.fmi.issuetracker.service;

import bg.sofia.uni.fmi.issuetracker.dto.input.project.CreateProjectDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.project.CreateProjectUserDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.project.UpdateProjectDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.project.ProjectDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.project.ProjectDetailsUserDTO;
import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectNotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectUserAlreadyInProjectException;
import bg.sofia.uni.fmi.issuetracker.exception.project.UserNotPartOfProjectException;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserNotFoundException;
import bg.sofia.uni.fmi.issuetracker.model.User;
import bg.sofia.uni.fmi.issuetracker.model.auth.Role;
import bg.sofia.uni.fmi.issuetracker.model.project.Project;
import bg.sofia.uni.fmi.issuetracker.model.project.ProjectUser;
import bg.sofia.uni.fmi.issuetracker.repository.ProjectRepository;
import bg.sofia.uni.fmi.issuetracker.repository.ProjectUserRepository;
import bg.sofia.uni.fmi.issuetracker.repository.UserRepository;
import bg.sofia.uni.fmi.issuetracker.repository.ticket.TicketRepository;
import bg.sofia.uni.fmi.issuetracker.service.contract.ProjectService;
import bg.sofia.uni.fmi.issuetracker.service.mapper.ProjectMapper;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service("projectService")
public class ProjectServiceImpl implements ProjectService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectUserRepository projectUserRepository;
    private final ProjectMapper projectMapper;
    private final TicketRepository ticketRepository;

    public ProjectServiceImpl(UserRepository userRepository, ProjectRepository projectRepository,
                              ProjectUserRepository projectUserRepository, ProjectMapper projectMapper, TicketRepository ticketRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.projectUserRepository = projectUserRepository;
        this.projectMapper = projectMapper;
        this.ticketRepository = ticketRepository;
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

        List<ProjectUser> users = projectUserRepository
                .findAllByProject(project);
        Map<User, Set<Role>> userRoles = new HashMap<>();
        for (ProjectUser pu : users) {
            userRoles.putIfAbsent(pu.getUser(), new HashSet<>());
            userRoles.get(pu.getUser()).add(pu.getRole());
        }

        return userRoles
                .entrySet()
                .stream()
                .map(e -> new ProjectDetailsUserDTO(e.getKey().getProfilePicturePath(),
                        e.getKey().getUsername(), e.getValue()))
                .toList();
    }

    @Override
    public ProjectDetailsUserDTO addProjectUser(String projectId, CreateProjectUserDTO dto) {
        Project project = getProject(projectId);
        User userToAdd = getUser(dto.username());

        if (projectRepository.isUserInProject(userToAdd, project, dto.role())) {
            throw new ProjectUserAlreadyInProjectException(
                    ExceptionMessages.ProjectUser.userAlreadyInProject(userToAdd.getUsername(), projectId, dto.role()));
        }

        ProjectUser projectUser = projectUserRepository.save(new ProjectUser(project, userToAdd, dto.role()));

        return new ProjectDetailsUserDTO(userToAdd.getProfilePicturePath(), projectUser.getUser().getUsername(), Set.of(projectUser.getRole()));
    }

    @Override
    @Transactional
    public void deleteProjectUser(String projectId, String username) {
        Project project = getProject(projectId);
        User user = getUser(username);

        if (!projectRepository.isUserInProject(user, project)) {
            throw new UserNotPartOfProjectException(
                    ExceptionMessages.ProjectUser.userNotFound(user.getUsername(), projectId));
        }

        projectUserRepository.deleteAllByProjectAndUser(project, user);
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
    public void addProject(CreateProjectDTO dto, String username) {
        User creator = getUser(username);
        Project project = new Project(dto.name());
        project.setCreatedBy(creator);

        projectRepository.save(project);
    }

    @Override
    @Transactional
    public void updateProject(String projectId, UpdateProjectDTO dto) {
        Project project = getProject(projectId);

        projectMapper.patchProjectFromDTO(dto, project);
        projectRepository.save(project);
    }

    @Override
    @Transactional
    public void deleteProject(String projectId) {
        Project project = getProject(projectId);

        ticketRepository.deleteAll(project.getTickets());
        projectUserRepository.deleteAll(project.getUsers());
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
}