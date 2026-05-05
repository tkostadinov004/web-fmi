package bg.sofia.uni.fmi.issuetracker.dto.output;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response body containing admin-only user information.")
public record AdminOnlyOutputUserDTO(
        @Schema(description = "Username of the user.", requiredMode = Schema.RequiredMode.REQUIRED)
        String username,
        @Schema(description = "First name of the user.", requiredMode = Schema.RequiredMode.REQUIRED)
        String firstName,
        @Schema(description = "Last name of the user.", requiredMode = Schema.RequiredMode.REQUIRED)
        String lastName,
        @Schema(description = "Email address of the user.", requiredMode = Schema.RequiredMode.REQUIRED, format = "email")
        String email,
        @Schema(description = "Indicates whether the user has admin privileges.", requiredMode = Schema.RequiredMode.REQUIRED)
        boolean isAdmin,
        @Schema(description = "Indicates whether the user has been deleted from the system.", requiredMode = Schema.RequiredMode.REQUIRED)
        boolean isDeleted) {
}
