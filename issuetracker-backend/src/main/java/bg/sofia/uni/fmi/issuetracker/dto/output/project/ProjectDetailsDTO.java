package bg.sofia.uni.fmi.issuetracker.dto.output.project;

import bg.sofia.uni.fmi.issuetracker.model.User;
import bg.sofia.uni.fmi.issuetracker.model.auth.Role;
import bg.sofia.uni.fmi.issuetracker.model.project.Project;
import bg.sofia.uni.fmi.issuetracker.model.project.ProjectUser;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Schema(description = "Response body with project details.")
public record ProjectDetailsDTO(
        @Schema(description = "Unique identifier code of the project.", requiredMode = Schema.RequiredMode.REQUIRED)
        String uuid,
        @Schema(description = "Name of the project.", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
        @Schema(description = "Description of the project.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String description,
        @Schema(description = "Timestamp when the project was created. Format: ISO 8601 (YYYY-MM-DDTHH:mm:ss).", requiredMode = Schema.RequiredMode.REQUIRED, type = "string", format = "date-time")
        LocalDateTime createTime,
        @Schema(description = "Information about the creator of the project", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        ProjectDetailsCreatorDTO creator,
        @Schema(description = "List of assigned users to the project.", requiredMode = Schema.RequiredMode.REQUIRED)
        List<ProjectDetailsUserDTO> users,
        @Schema(description = "List of tickets added in the project", requiredMode = Schema.RequiredMode.REQUIRED)
        List<ProjectDetailsAddedTicketDTO> tickets
) {

    public static ProjectDetailsDTO from(Project project) {
        Map<User, Set<Role>> userRoles = new HashMap<>();
        for (ProjectUser pu : project.getUsers()) {
            userRoles.putIfAbsent(pu.getUser(), new HashSet<>());
            userRoles.get(pu.getUser()).add(pu.getRole());
        }

        List<ProjectDetailsUserDTO> users = userRoles
                .entrySet()
                .stream()
                .map(e -> new ProjectDetailsUserDTO(
                        e.getKey().getProfilePicturePath(),
                        e.getKey().getUsername(),
                        e.getValue()
                ))
                .toList();

        List<ProjectDetailsAddedTicketDTO> tickets = project.getTickets()
                .stream()
                .map(ticket -> new ProjectDetailsAddedTicketDTO(ticket.getCode(), ticket.getTitle()))
                .toList();

        return new ProjectDetailsDTO(
                project.getUuid(),
                project.getName(),
                project.getDescription(),
                project.getCreatedAt(),
                new ProjectDetailsCreatorDTO(project.getCreatedBy().getProfilePicturePath(),
                        project.getCreatedBy().getUsername(), project.getCreatedBy().getEmail()),
                users,
                tickets
        );
    }
}
