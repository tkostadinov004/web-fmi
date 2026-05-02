package bg.sofia.uni.fmi.issuetracker.model.ticket;

import bg.sofia.uni.fmi.issuetracker.model.auth.User;
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
    @JoinColumn(name = "ticket")
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "author")
    private User author;

    @Column(name = "content", nullable = false, length = 5000)
    private String content;

    @Column(name = "create_date")
    private LocalDateTime createDate = LocalDateTime.now();

    public TicketComment() {

    }

    public TicketComment(String uuid, Ticket ticket, User author, String content, LocalDateTime createDate) {
        this.uuid = uuid;
        this.ticket = ticket;
        this.author = author;
        this.content = content;
        this.createDate = createDate;
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

    public LocalDateTime getCreateDate() {
        return createDate;
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
