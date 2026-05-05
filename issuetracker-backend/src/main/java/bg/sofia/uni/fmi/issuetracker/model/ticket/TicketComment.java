package bg.sofia.uni.fmi.issuetracker.model.ticket;

import bg.sofia.uni.fmi.issuetracker.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "ticket_comments")
public class TicketComment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid")
    private String uuid;

    @ManyToOne
    @JoinColumn(name = "ticket_code")
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "author_username")
    private User author;

    @Column(name = "content", nullable = false, length = 5000)
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public TicketComment() {

    }

    public TicketComment(Ticket ticket, User author, String content, LocalDateTime createdAt) {
        this.ticket = ticket;
        this.author = author;
        this.content = content;
        this.createdAt = createdAt;
    }

    public String getUuid() {
        return uuid;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TicketComment that)) return false;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }
}
