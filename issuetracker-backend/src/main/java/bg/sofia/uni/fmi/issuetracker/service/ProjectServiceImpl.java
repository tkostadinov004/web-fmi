package bg.sofia.uni.fmi.issuetracker.service;

import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectDoesNotExistException;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserNotFoundException;
import bg.sofia.uni.fmi.issuetracker.model.User;
import bg.sofia.uni.fmi.issuetracker.model.auth.Role;
import bg.sofia.uni.fmi.issuetracker.model.project.Project;
import bg.sofia.uni.fmi.issuetracker.repository.ProjectRepository;
import bg.sofia.uni.fmi.issuetracker.repository.UserRepository;
import bg.sofia.uni.fmi.issuetracker.service.contract.ProjectService;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import org.springframework.stereotype.Service;

import java.util.Collection;
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
            throw new ProjectDoesNotExistException(ExceptionMessages.Project.projectNotFound(projectId));
        }

        return projectRepository.isUserInProject(user.get(), project.get());
    }

    @Override
    public boolean hasRoles(String username, String projectId, Collection<Role> roles, boolean strict) {
        Optional<User> user = userRepository.findById(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException(ExceptionMessages.User.userNotFound(username));
        }

        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isEmpty()) {
            throw new ProjectDoesNotExistException(ExceptionMessages.Project.projectNotFound(projectId));
        }

        return strict ? projectRepository.hasRolesStrict(user.get(), project.get(), roles) :
                projectRepository.hasRoles(user.get(), project.get(), roles);
    }
}