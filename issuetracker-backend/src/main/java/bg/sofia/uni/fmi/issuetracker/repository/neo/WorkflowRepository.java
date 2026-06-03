package bg.sofia.uni.fmi.issuetracker.repository.neo;

import bg.sofia.uni.fmi.issuetracker.dto.input.project.workflow.ProjectWorkflowDTO;

import java.util.List;

public interface WorkflowRepository {
    ProjectWorkflowDTO getWorkflow(String projectId);

    void createWorkflow(String projectId, ProjectWorkflowDTO dto);

    void deleteWorkflow(String projectId);

    boolean isTransitionPossible(String projectId, String source, String target);

    List<String> getStatuses(String projectId);
}
