package bg.sofia.uni.fmi.issuetracker.dto.output;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Response body containing detailed information about a user profile.")
public record UserDetailsDTO(
        @Schema(description = "Path or URL to the user's profile picture.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String profilePicture,
        @Schema(description = "Username of the user.", requiredMode = Schema.RequiredMode.REQUIRED)
        String username,
        @Schema(description = "First name of the user.", requiredMode = Schema.RequiredMode.REQUIRED)
        String firstName,
        @Schema(description = "Last name of the user.", requiredMode = Schema.RequiredMode.REQUIRED)
        String lastName,
        @Schema(description = "Email address of the user.", requiredMode = Schema.RequiredMode.REQUIRED, format = "email")
        String email,
        @Schema(description = "Company name associated with the user. Optional.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String companyName,
        @Schema(description = "Indicates whether the user has admin privileges.", requiredMode = Schema.RequiredMode.REQUIRED)
        boolean isAdmin,
        @Schema(description = "List of projects the user is associated with.", requiredMode = Schema.RequiredMode.REQUIRED)
        List<OutputUserProjectDTO> projects) {
}
