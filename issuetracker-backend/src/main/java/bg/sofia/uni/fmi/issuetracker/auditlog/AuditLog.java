package bg.sofia.uni.fmi.issuetracker.auditlog;

import bg.sofia.uni.fmi.issuetracker.model.auditlog.AuditLogType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditLog {
    String message() default "";

    AuditLogType type();
}
