package bg.sofia.uni.fmi.issuetracker.dto.output.auditlog;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User information included in an audit log response.")
public record OutputAuditLogUserDTO(
        @Schema(description = "Username of the user who generated the audit log entry.", requiredMode = Schema.RequiredMode.REQUIRED)
        String username,
        @Schema(description = "Path or URL to the user's profile picture.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String profilePicturePath) {
}
