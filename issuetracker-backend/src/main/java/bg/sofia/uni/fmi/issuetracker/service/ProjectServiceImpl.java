package bg.sofia.uni.fmi.issuetracker.service;

import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectDoesNotExistException;
import bg.sofia.uni.fmi.issuetracker.model.auth.Project;
import bg.sofia.uni.fmi.issuetracker.model.auth.Role;
import bg.sofia.uni.fmi.issuetracker.model.auth.User;
import bg.sofia.uni.fmi.issuetracker.repository.ProjectRepository;
import bg.sofia.uni.fmi.issuetracker.service.contract.ProjectService;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("projectService")
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public boolean isMemberOf(User user, String projectId) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isEmpty()) {
            throw new ProjectDoesNotExistException(ExceptionMessages.Project.projectDoesNotExist(projectId));
        }

        return projectRepository.isUserInProject(user, project.get());
    }

    @Override
    public boolean hasRole(Authentication authentication, String projectId, String roleString) {
        User user = (User) authentication.getPrincipal();
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isEmpty()) {
            throw new ProjectDoesNotExistException(ExceptionMessages.Project.projectDoesNotExist(projectId));
        }

        Role role = Role.valueOf(roleString);
        return projectRepository.hasRole(user, project.get(), role);
    }
}