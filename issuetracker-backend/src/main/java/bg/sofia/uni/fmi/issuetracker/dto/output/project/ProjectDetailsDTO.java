package bg.sofia.uni.fmi.issuetracker.dto.output.project;

import bg.sofia.uni.fmi.issuetracker.model.project.Project;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

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
        List<ProjectDetailsUserDTO> users = project.getUsers()
                .stream()
                .map(projectUser -> new ProjectDetailsUserDTO(
                        projectUser.getUser().getProfilePicturePath(),
                        projectUser.getUser().getUsername()
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
