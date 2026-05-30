package bg.sofia.uni.fmi.issuetracker.dto.input.project.workflow;

import java.util.List;

public record ProjectWorkflowDTO(List<String> workflowStatuses,
                                 String initialStatus,
                                 List<WorkflowTransitionDTO> transitions) {
}
