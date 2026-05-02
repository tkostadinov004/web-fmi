package bg.sofia.uni.fmi.issuetracker.dto.ticket;

import bg.sofia.uni.fmi.issuetracker.model.auth.User;
import bg.sofia.uni.fmi.issuetracker.model.ticket.Ticket;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class TicketCommentRequest {

    @NotNull
    @NotBlank
    private String uuid;

    @NotBlank
    @NotBlank
    private String content;

    @NotNull
    private User author;

    @NotNull
    private Ticket ticket;

    @NotNull
    private LocalDateTime createDate;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }
}
