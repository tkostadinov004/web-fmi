package bg.sofia.uni.fmi.issuetracker.service.contract;

import bg.sofia.uni.fmi.issuetracker.dto.input.ticket.CreateTicketCommentDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.ticket.UpdateTicketCommentDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.ticket.TicketCommentDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.exception.OwnershipMismatchException;
import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectNotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketCommentNotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketNotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserNotFoundException;
import org.springframework.data.domain.Page;

public interface TicketCommentService {
    /**
     * Retrieves paginated comments for a specific ticket.
     *
        * @param projectId the UUID of the project the ticket belongs to
        * @param ticketCode the code of the ticket to retrieve comments for (scoped to the project)
        * @param pageNumber the page number (1-indexed), defaults to 1 if less than or equal to 0
        * @param pageSize   the number of comments per page, defaults to a default page size if less than or equal to 0
     * @return a {@link Page} of {@link TicketCommentDetailsDTO} containing paginated ticket comments
        * @throws ProjectNotFoundException if the project does not exist
        * @throws TicketNotFoundException if no ticket with the given code exists in the given project
     */
        Page<TicketCommentDetailsDTO> getAllCommentsForTicket(String projectId, String ticketCode, int pageNumber, int pageSize);

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
        * @param projectId the UUID of the project the ticket belongs to
        * @param ticketCode     the code of the ticket to add the comment to (scoped to the project)
        * @param dto            the {@link CreateTicketCommentDTO} containing comment data
        * @throws UserNotFoundException   if the author username does not exist
        * @throws ProjectNotFoundException if the project does not exist
        * @throws TicketNotFoundException if no ticket with the given code exists in the given project
     */
        void addTicketComment(String authorUsername, String projectId, String ticketCode, CreateTicketCommentDTO dto);

    /**
     * Updates an existing ticket comment.
     *
     * @param commentUuid the UUID of the comment to update
     * @param dto         the {@link UpdateTicketCommentDTO} containing updated comment data
     * @param username    the username of the currently logged user
     * @throws TicketCommentNotFoundException if no comment with the given UUID exists
     * @throws OwnershipMismatchException     if a user different from the author of the comment tries modifying the comment
     */
    void updateTicketComment(String commentUuid, UpdateTicketCommentDTO dto, String username);

    /**
     * Deletes a ticket comment.
     *
     * @param commentUuid the UUID of the comment to delete
     * @throws TicketCommentNotFoundException if no comment with the given UUID exists
     * @throws OwnershipMismatchException     if a user different from the author of the comment tries deleting the comment
     */
    void deleteTicketComment(String commentUuid, String username);
}
