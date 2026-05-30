package bg.sofia.uni.fmi.issuetracker.service;

import bg.sofia.uni.fmi.issuetracker.dto.input.auditlog.InputAuditLogDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.auditlog.OutputAuditLogDTO;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserNotFoundException;
import bg.sofia.uni.fmi.issuetracker.model.User;
import bg.sofia.uni.fmi.issuetracker.model.auditlog.AuditLog;
import bg.sofia.uni.fmi.issuetracker.model.auditlog.AuditLogType;
import bg.sofia.uni.fmi.issuetracker.repository.AuditLogRepository;
import bg.sofia.uni.fmi.issuetracker.repository.UserRepository;
import bg.sofia.uni.fmi.issuetracker.utils.Constants;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Function;

import static bg.sofia.uni.fmi.issuetracker.TestData.TEST_USER;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuditLogServiceImplTests {
    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogServiceImpl auditLogService;

    @Test
    void testGetAll_CallsRepositoryWithRequestedPage() {
        Page<AuditLog> auditLogs = mock(Page.class);
        Page<OutputAuditLogDTO> dtoPage = mock(Page.class);
        when(auditLogRepository.findAllByUser(any(User.class), any(Pageable.class))).thenReturn(auditLogs);
        when(auditLogs.map(any(Function.class))).thenReturn(dtoPage);

        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));
        auditLogService.getAll(TEST_USER.getUsername(), 2, 5);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(auditLogRepository, times(1)).findAllByUser(any(User.class), pageableCaptor.capture());
        Pageable pageable = pageableCaptor.getValue();

        assertEquals(1, pageable.getPageNumber());
        assertEquals(5, pageable.getPageSize());
        verify(auditLogs, times(1)).map(any());
    }

    @Test
    void testGetAll_UsesDefaultPageValuesWhenInvalid() {
        Page<AuditLog> auditLogs = mock(Page.class);
        Page<OutputAuditLogDTO> dtoPage = mock(Page.class);
        when(auditLogRepository.findAllByUser(any(User.class), any(Pageable.class))).thenReturn(auditLogs);
        when(auditLogs.map(any(Function.class))).thenReturn(dtoPage);

        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));
        auditLogService.getAll(TEST_USER.getUsername(), 0, -1);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(auditLogRepository, times(1)).findAllByUser(any(User.class), pageableCaptor.capture());
        Pageable pageable = pageableCaptor.getValue();

        assertEquals(0, pageable.getPageNumber());
        assertEquals(Integer.parseInt(Constants.DEFAULT_PAGE_SIZE), pageable.getPageSize());
    }

    @Test
    void testGetAll_ThrowsWhenUserNotFound() {
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> auditLogService.getAll(TEST_USER.getUsername(), 1, 5))
                .isExactlyInstanceOf(UserNotFoundException.class)
                .hasMessage(ExceptionMessages.User.userNotFound(TEST_USER.getUsername()));
    }

    @Test
    void testAddAuditLog_SavesEntryWhenUserFound() {
        User user = new User();
        user.setUsername(TEST_USER.getUsername());
        String message = "Test entry";
        LocalDateTime timestamp = LocalDateTime.now();
        InputAuditLogDTO dto = new InputAuditLogDTO(message, AuditLogType.CREATE, timestamp, user.getUsername());

        when(userRepository.findById(user.getUsername())).thenReturn(Optional.of(user));
        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        auditLogService.addAuditLog(dto);

        ArgumentCaptor<AuditLog> auditLogCaptor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository, times(1)).save(auditLogCaptor.capture());
        AuditLog savedLog = auditLogCaptor.getValue();

        assertEquals(message, savedLog.getMessage());
        assertEquals(AuditLogType.CREATE, savedLog.getType());
        assertEquals(timestamp, savedLog.getTimestamp());
        assertEquals(user, savedLog.getUser());
    }

    @Test
    void testAddAuditLog_ThrowsWhenUserNotFound() {
        InputAuditLogDTO dto = new InputAuditLogDTO("Test entry", AuditLogType.CREATE, LocalDateTime.now(), "unknown");
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> auditLogService.addAuditLog(dto))
                .isExactlyInstanceOf(UserNotFoundException.class)
                .hasMessage(ExceptionMessages.User.userNotFound(dto.username()));
    }
}
