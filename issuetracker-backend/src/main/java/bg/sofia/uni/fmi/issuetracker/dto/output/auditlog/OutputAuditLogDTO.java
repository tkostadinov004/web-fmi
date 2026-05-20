package bg.sofia.uni.fmi.issuetracker.dto.output.auditlog;

import bg.sofia.uni.fmi.issuetracker.model.auditlog.AuditLogType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Response body containing audit log entry data.")
public record OutputAuditLogDTO(
        @Schema(description = "Unique identifier of the audit log entry.", requiredMode = Schema.RequiredMode.REQUIRED)
        String uuid,
        @Schema(description = "Audit log message describing the event.", requiredMode = Schema.RequiredMode.REQUIRED)
        String message,
        @Schema(description = "Type of audit event.", requiredMode = Schema.RequiredMode.REQUIRED)
        AuditLogType type,
        @Schema(description = "Timestamp when the audited event occurred.", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime timestamp,
        @Schema(description = "User metadata associated with the audit log entry.", requiredMode = Schema.RequiredMode.REQUIRED)
        OutputAuditLogUserDTO user) {
}
