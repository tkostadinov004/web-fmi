package bg.sofia.uni.fmi.issuetracker.service;

import bg.sofia.uni.fmi.issuetracker.dto.input.auditlog.InputAuditLogDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.auditlog.OutputAuditLogDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.auditlog.OutputAuditLogUserDTO;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserNotFoundException;
import bg.sofia.uni.fmi.issuetracker.model.User;
import bg.sofia.uni.fmi.issuetracker.model.auditlog.AuditLog;
import bg.sofia.uni.fmi.issuetracker.repository.AuditLogRepository;
import bg.sofia.uni.fmi.issuetracker.repository.UserRepository;
import bg.sofia.uni.fmi.issuetracker.service.contract.AuditLogService;
import bg.sofia.uni.fmi.issuetracker.utils.Constants;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service implementation for audit log operations.
 *
 * <p>Provides methods to retrieve paginated audit logs and to persist new audit
 * log entries while validating that the associated user exists.</p>
 */
@Service
public class AuditLogServiceImpl implements AuditLogService {
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    public AuditLogServiceImpl(UserRepository userRepository, AuditLogRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public Page<OutputAuditLogDTO> getAll(String username, int pageNumber, int pageSize) {
        Optional<User> user = userRepository.findById(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException(ExceptionMessages.User.userNotFound(username));
        }

        pageNumber = pageNumber <= 0 ? Integer.parseInt(Constants.DEFAULT_PAGE_NUMBER) : pageNumber;
        pageSize = pageSize <= 0 ? Integer.parseInt(Constants.DEFAULT_PAGE_SIZE) : pageSize;

        Pageable pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return auditLogRepository
                .findAllByUser(user.get(), pageRequest)
                .map(au -> new OutputAuditLogDTO(au.getUuid(), au.getMessage(), au.getType(), au.getTimestamp(),
                        new OutputAuditLogUserDTO(au.getUser().getUsername(), au.getUser().getProfilePicturePath())));
    }

    @Override
    public void addAuditLog(InputAuditLogDTO dto) {
        Optional<User> user = userRepository.findById(dto.username());
        if (user.isEmpty()) {
            throw new UserNotFoundException(ExceptionMessages.User.userNotFound(dto.username()));
        }

        AuditLog auditLog = new AuditLog(dto.message(), dto.type(), dto.timestamp(), user.get());
        auditLogRepository.save(auditLog);
    }
}
