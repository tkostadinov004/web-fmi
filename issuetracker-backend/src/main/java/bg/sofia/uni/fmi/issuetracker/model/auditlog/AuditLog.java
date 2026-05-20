package bg.sofia.uni.fmi.issuetracker.model.auditlog;

import bg.sofia.uni.fmi.issuetracker.model.User;
import bg.sofia.uni.fmi.issuetracker.utils.VisibleForTesting;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid")
    private String uuid;

    @Column(name = "message")
    private String message;

    @Column(name = "type")
    @Enumerated(value = EnumType.STRING)
    private AuditLogType type;

    @Column(name = "time")
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "user_username")
    private User user;

    public AuditLog() {
    }

    public AuditLog(String message, AuditLogType type, LocalDateTime timestamp, User user) {
        this.message = message;
        this.type = type;
        this.timestamp = timestamp;
        this.user = user;
    }

    @VisibleForTesting
    public AuditLog(String uuid, String message, AuditLogType type, LocalDateTime timestamp, User user) {
        this.uuid = uuid;
        this.message = message;
        this.type = type;
        this.timestamp = timestamp;
        this.user = user;
    }

    public String getUuid() {
        return uuid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AuditLogType getType() {
        return type;
    }

    public void setType(AuditLogType type) {
        this.type = type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AuditLog auditLog = (AuditLog) o;
        return Objects.equals(uuid, auditLog.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }
}
