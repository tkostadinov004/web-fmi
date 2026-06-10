package bg.sofia.uni.fmi.issuetracker.repository.neo;

import bg.sofia.uni.fmi.issuetracker.dto.input.project.workflow.ProjectWorkflowDTO;
import bg.sofia.uni.fmi.issuetracker.exception.project.WorkflowAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.exception.project.WorkflowNotFoundException;

import java.util.List;

/**
 * Repository interface for Neo4j workflow operations within a project.
 * <p>
 * This repository manages workflow status nodes and transitions for a project
 * stored in the Neo4j graph database.
 */
public interface WorkflowRepository {

    /**
     * Checks whether a project has a workflow.
     *
     * @param projectId the unique identifier of the project
     * @return {@code true} if the project has a workflow, {@code false} otherwise
     */
    boolean hasWorkflow(String projectId);

    /**
     * Retrieves the initial (default) status for the specified project.
     *
     * @param projectId the unique identifier of the project
     * @return the initial status for the project
     * @throws WorkflowNotFoundException if the project does not have a workflow
     */
    String getInitialStatus(String projectId);

    /**
     * Retrieves the workflow configuration for the specified project.
     *
     * @param projectId the unique identifier of the project
     * @return the workflow configuration for the project
     * @throws WorkflowNotFoundException if the project does not have a workflow
     */
    ProjectWorkflowDTO getWorkflow(String projectId);

    /**
     * Creates or updates the workflow structure for the specified project.
     *
     * @param projectId the unique identifier of the project
     * @param dto       the workflow definition to persist
     * @throws WorkflowAlreadyExistsException if the project already has a workflow
     */
    void createWorkflow(String projectId, ProjectWorkflowDTO dto);

    /**
     * Deletes all workflow status nodes and transitions associated with the project.
     *
     * @param projectId the unique identifier of the project
     */
    void deleteWorkflow(String projectId);

    /**
     * Determines whether a transition between two statuses is valid for the given project.
     *
     * @param projectId the unique identifier of the project
     * @param source    the current status name
     * @param target    the target status name
     * @return {@code true} if the transition is permitted; otherwise {@code false}
     */
    boolean isTransitionPossible(String projectId, String source, String target);

    /**
     * Retrieves all status names defined for the specified project's workflow.
     *
     * @param projectId the unique identifier of the project
     * @return list of workflow status names
     */
    List<String> getStatuses(String projectId);
}
