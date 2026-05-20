package bg.sofia.uni.fmi.issuetracker.dto.input.auditlog;

import bg.sofia.uni.fmi.issuetracker.model.auditlog.AuditLogType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Request body used for creating a new audit log entry.")
public record InputAuditLogDTO(
        @Schema(description = "Audit log message describing the event.", requiredMode = Schema.RequiredMode.REQUIRED)
        String message,
        @Schema(description = "Type of audit event.", requiredMode = Schema.RequiredMode.REQUIRED)
        AuditLogType type,
        @Schema(description = "Timestamp when the event occurred.", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime timestamp,
        @Schema(description = "Username of the user associated with this audit log entry.", requiredMode = Schema.RequiredMode.REQUIRED)
        String username) {
}
