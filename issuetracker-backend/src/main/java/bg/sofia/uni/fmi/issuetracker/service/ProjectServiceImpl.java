package bg.sofia.uni.fmi.issuetracker.service;

import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectDoesNotExistException;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserNotFoundException;
import bg.sofia.uni.fmi.issuetracker.model.auth.Project;
import bg.sofia.uni.fmi.issuetracker.model.auth.Role;
import bg.sofia.uni.fmi.issuetracker.model.auth.User;
import bg.sofia.uni.fmi.issuetracker.repository.ProjectRepository;
import bg.sofia.uni.fmi.issuetracker.repository.UserRepository;
import bg.sofia.uni.fmi.issuetracker.service.contract.ProjectService;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("projectService")
public class ProjectServiceImpl implements ProjectService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public ProjectServiceImpl(UserRepository userRepository, ProjectRepository projectRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public boolean isMemberOf(String username, String projectId) {
        Optional<User> user = userRepository.findById(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException(ExceptionMessages.User.userNotFound(username));
        }

        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isEmpty()) {
            throw new ProjectDoesNotExistException(ExceptionMessages.Project.projectDoesNotExist(projectId));
        }

        return projectRepository.isUserInProject(user.get(), project.get());
    }

    @Override
    public boolean hasRole(Authentication authentication, String projectId, String roleString) {
        return hasRole((String) authentication.getPrincipal(), projectId, roleString);
    }

    @Override
    public boolean hasRole(String username, String projectId, String roleString) {
        Optional<User> user = userRepository.findById(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException(ExceptionMessages.User.userNotFound(username));
        }

        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isEmpty()) {
            throw new ProjectDoesNotExistException(ExceptionMessages.Project.projectDoesNotExist(projectId));
        }

        Role role = Role.valueOf(roleString);
        return projectRepository.hasRole(user.get(), project.get(), role);
    }
}