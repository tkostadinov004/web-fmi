package bg.sofia.uni.fmi.issuetracker.service.contract;

import bg.sofia.uni.fmi.issuetracker.dto.input.auditlog.InputAuditLogDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.auditlog.OutputAuditLogDTO;
import org.springframework.data.domain.Page;

public interface AuditLogService {
    Page<OutputAuditLogDTO> getAll(String username, int pageNumber, int pageSize);

    void addAuditLog(InputAuditLogDTO dto);
}
