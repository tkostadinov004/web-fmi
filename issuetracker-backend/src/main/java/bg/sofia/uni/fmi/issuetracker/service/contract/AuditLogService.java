package bg.sofia.uni.fmi.issuetracker.service.contract;

import bg.sofia.uni.fmi.issuetracker.dto.input.auditlog.InputAuditLogDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.auditlog.OutputAuditLogDTO;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserNotFoundException;
import org.springframework.data.domain.Page;

public interface AuditLogService {
    /**
     * Retrieves a paginated list of audit log entries for the given user.
     *
     * <p>Negative or zero values for {@code pageNumber} and {@code pageSize}
     * are normalized to default pagination values.</p>
     *
     * @param username   the username to filter audit logs by
     * @param pageNumber the requested page number, starting at 1
     * @param pageSize   the maximum number of entries per page
     * @return a page of {@link OutputAuditLogDTO} results
     * @throws UserNotFoundException when the user does not exist
     */
    Page<OutputAuditLogDTO> getAll(String username, int pageNumber, int pageSize);

    /**
     * Records a new audit log entry.
     *
     * @param dto the audit log data transfer object containing details of the event
     * @throws UserNotFoundException when the referenced user does not exist
     */
    void addAuditLog(InputAuditLogDTO dto);
}
