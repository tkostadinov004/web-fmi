package bg.sofia.uni.fmi.issuetracker.dto.ticket;

import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketComment;

import java.time.LocalDateTime;

public class TicketCommentResponse {

    private String uuid;
    private String content;
    private LocalDateTime createDate;
    private String ticketUuid;
    private String authorUsername;

    public static TicketCommentResponse from(TicketComment ticketComment) {

        TicketCommentResponse response = new TicketCommentResponse();

        response.uuid = ticketComment.getUuid();
        response.content = ticketComment.getContent();
        response.createDate = ticketComment.getCreateDate();
        response.ticketUuid = ticketComment.getTicket().getUuid();
        response.authorUsername = ticketComment.getAuthor().getUsername();

        return response;
    }

    public String getUuid() {
        return uuid;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public String getTicketUuid() {
        return ticketUuid;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }
}
