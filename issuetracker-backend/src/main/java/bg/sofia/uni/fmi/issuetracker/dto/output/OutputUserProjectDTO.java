package bg.sofia.uni.fmi.issuetracker.dto.output;

import bg.sofia.uni.fmi.issuetracker.model.auth.Role;

public record OutputUserProjectDTO(String projectName, String projectId, Role role) {
}
