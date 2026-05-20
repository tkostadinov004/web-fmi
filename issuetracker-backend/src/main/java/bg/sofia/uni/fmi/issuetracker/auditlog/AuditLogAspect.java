package bg.sofia.uni.fmi.issuetracker.auditlog;

import bg.sofia.uni.fmi.issuetracker.dto.input.auditlog.InputAuditLogDTO;
import bg.sofia.uni.fmi.issuetracker.service.contract.AuditLogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
public class AuditLogAspect {
    private final AuditLogService auditLogService;

    public AuditLogAspect(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @AfterReturning("@annotation(AuditLog)")
    public void addLog(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        AuditLog annotation = method.getAnnotation(AuditLog.class);
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        InputAuditLogDTO dto = new InputAuditLogDTO(annotation.message(), annotation.type(),
                LocalDateTime.now(), username);
        auditLogService.addAuditLog(dto);
    }
}
