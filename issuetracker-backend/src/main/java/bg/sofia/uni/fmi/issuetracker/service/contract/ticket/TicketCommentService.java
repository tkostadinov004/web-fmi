package bg.sofia.uni.fmi.issuetracker.service.contract.ticket;

import bg.sofia.uni.fmi.issuetracker.dto.input.ticket.CreateTicketCommentDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.ticket.UpdateTicketCommentDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.ticket.TicketCommentDetailsDTO;
import org.springframework.data.domain.Page;

public interface TicketCommentService {
    /**
     * Retrieves paginated comments for a specific ticket.
     *
     * @param ticketCode the code of the ticket to retrieve comments for
     * @param pageNumber the page number (1-indexed), defaults to 1 if less than or equal to 0
     * @param pageSize the number of comments per page, defaults to a default page size if less than or equal to 0
     * @return a {@link Page} of {@link TicketCommentDetailsDTO} containing paginated ticket comments
     * @throws TicketNotFoundException if no ticket with the given code exists
     */
    Page<TicketCommentDetailsDTO> getAllCommentsForTicket(String ticketCode, int pageNumber, int pageSize);

    /**
     * Retrieves a specific ticket comment by its UUID.
     *
     * @param commentUuid the UUID of the comment to retrieve
     * @return a {@link TicketCommentDetailsDTO} containing the comment details
     * @throws TicketCommentNotFoundException if no comment with the given UUID exists
     */
    TicketCommentDetailsDTO getTicketComment(String commentUuid);

    /**
     * Adds a new comment to a ticket.
     *
     * @param authorUsername the username of the comment author
     * @param ticketCode the code of the ticket to add the comment to
     * @param dto the {@link CreateTicketCommentDTO} containing comment data
     * @throws UserNotFoundException if the author username does not exist
     * @throws TicketNotFoundException if no ticket with the given code exists
     */
    void addTicketComment(String authorUsername, String ticketCode, CreateTicketCommentDTO dto);

    /**
     * Updates an existing ticket comment.
     *
     * @param commentUuid the UUID of the comment to update
     * @param dto the {@link UpdateTicketCommentDTO} containing updated comment data
     * @throws TicketCommentNotFoundException if no comment with the given UUID exists
     */
    void updateTicketComment(String commentUuid, UpdateTicketCommentDTO dto);

    /**
     * Deletes a ticket comment.
     *
     * @param commentUuid the UUID of the comment to delete
     * @throws TicketCommentNotFoundException if no comment with the given UUID exists
     */
    void deleteTicketComment(String commentUuid);
}
