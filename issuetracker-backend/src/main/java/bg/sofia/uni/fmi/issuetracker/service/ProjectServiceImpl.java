package bg.sofia.uni.fmi.issuetracker.service;

import bg.sofia.uni.fmi.issuetracker.dto.input.project.CreateProjectDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.project.CreateProjectUserDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.project.UpdateProjectDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.project.workflow.ProjectWorkflowDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.project.workflow.WorkflowTransitionDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.project.ProjectDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.project.ProjectDetailsUserDTO;
import bg.sofia.uni.fmi.issuetracker.exception.project.InvalidWorkflowException;
import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectNotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectUserAlreadyInProjectException;
import bg.sofia.uni.fmi.issuetracker.exception.project.UnauthorizedProjectModificationException;
import bg.sofia.uni.fmi.issuetracker.exception.project.UserNotPartOfProjectException;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserNotFoundException;
import bg.sofia.uni.fmi.issuetracker.model.User;
import bg.sofia.uni.fmi.issuetracker.model.auth.Role;
import bg.sofia.uni.fmi.issuetracker.model.project.Project;
import bg.sofia.uni.fmi.issuetracker.model.project.ProjectUser;
import bg.sofia.uni.fmi.issuetracker.repository.ProjectRepository;
import bg.sofia.uni.fmi.issuetracker.repository.ProjectUserRepository;
import bg.sofia.uni.fmi.issuetracker.repository.TicketRepository;
import bg.sofia.uni.fmi.issuetracker.repository.UserRepository;
import bg.sofia.uni.fmi.issuetracker.repository.neo.NeoProjectRepository;
import bg.sofia.uni.fmi.issuetracker.repository.neo.WorkflowRepository;
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

@Service
public class ProjectServiceImpl implements ProjectService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectUserRepository projectUserRepository;
    private final ProjectMapper projectMapper;
    private final TicketRepository ticketRepository;
    private final NeoProjectRepository neoProjectRepository;
    private final WorkflowRepository workflowRepository;

    public ProjectServiceImpl(UserRepository userRepository, ProjectRepository projectRepository,
                              ProjectUserRepository projectUserRepository, ProjectMapper projectMapper, TicketRepository ticketRepository, NeoProjectRepository neoProjectRepository, WorkflowRepository workflowRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.projectUserRepository = projectUserRepository;
        this.projectMapper = projectMapper;
        this.ticketRepository = ticketRepository;
        this.neoProjectRepository = neoProjectRepository;
        this.workflowRepository = workflowRepository;
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
    public ProjectDetailsUserDTO addProjectUser(String projectId, CreateProjectUserDTO dto, String addInitiatorUsername) {
        Project project = getProject(projectId);
        User initiator = getUser(addInitiatorUsername);
        if (!projectRepository.isUserInProject(initiator, project, Role.TEAM_LEAD)) {
            throw new UnauthorizedProjectModificationException(ExceptionMessages.ProjectUser.cannotAddUserToProject(project.getUuid(), addInitiatorUsername));
        }
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
    public void deleteProjectUser(String projectId, String username, String actionInitiatorUsername) {
        Project project = getProject(projectId);
        User initiator = getUser(actionInitiatorUsername);
        if (!projectRepository.isUserInProject(initiator, project, Role.TEAM_LEAD)) {
            throw new UnauthorizedProjectModificationException(ExceptionMessages.ProjectUser.cannotRemoveUserFromProject(project.getUuid(), actionInitiatorUsername));
        }
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
    @Transactional
    public void addProject(CreateProjectDTO dto, String username) {
        User creator = getUser(username);
        Project project = new Project(dto.name());
        project.setCreatedBy(creator);

        project = projectRepository.save(project);
        project.addUser(new ProjectUser(project, creator, Role.TEAM_LEAD));
        projectRepository.save(project);
        neoProjectRepository.addProject(project.getUuid());
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
        workflowRepository.deleteWorkflow(projectId);
        neoProjectRepository.deleteProject(projectId);
    }

    @Override
    public void addProjectWorkflow(String projectId, ProjectWorkflowDTO dto) {
        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException(ExceptionMessages.Project.projectNotFound(projectId));
        }

        for (WorkflowTransitionDTO transition : dto.transitions()) {
            if (!dto.workflowStatuses().contains(transition.source())) {
                throw new InvalidWorkflowException(ExceptionMessages.Project.invalidSourceStatus(dto.workflowStatuses()));
            }
            if (!dto.workflowStatuses().contains(transition.target())) {
                throw new InvalidWorkflowException(ExceptionMessages.Project.invalidTargetStatus(dto.workflowStatuses()));
            }
            if (transition.source().equals(transition.target())) {
                throw new InvalidWorkflowException(ExceptionMessages.Project.transitionBuckle());
            }
        }

        workflowRepository.createWorkflow(projectId, dto);
    }

    @Override
    public ProjectWorkflowDTO getProjectWorkflow(String projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException(ExceptionMessages.Project.projectNotFound(projectId));
        }

        return workflowRepository.getWorkflow(projectId);
    }

    @Override
    public void deleteProjectWorkflow(String projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException(ExceptionMessages.Project.projectNotFound(projectId));
        }

        workflowRepository.deleteWorkflow(projectId);
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