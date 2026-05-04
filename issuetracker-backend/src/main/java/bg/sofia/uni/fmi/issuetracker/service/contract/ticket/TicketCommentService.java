package bg.sofia.uni.fmi.issuetracker.service.contract.ticket;

import bg.sofia.uni.fmi.issuetracker.dto.ticket.TicketCommentRequest;
import bg.sofia.uni.fmi.issuetracker.dto.ticket.TicketCommentResponse;
import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectDoesNotExistException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketCommentAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketCommentNotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketCommentNotInTickedException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketNotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketNotInProjectException;

import java.util.List;

public interface TicketCommentService {


    /**
     * Returns all comments of a ticket
     *
     * @param projectUuid the uuid of the project
     * @param ticketUuid  the uuid of the ticket
     * @return {@link List < TicketCommentResponse >}
     * @throws ProjectDoesNotExistException if no project with the given uuid exists
     */
    List<TicketCommentResponse> getAllTicketCommentsByProjectAndTicket(String projectUuid, String ticketUuid);

    /**
     * Returns the TicketComment with given uuid
     *
     * @param uuid the uuid of the ticket
     * @return {@link TicketCommentResponse}
     * @throws TicketCommentNotFoundException if the TicketComment is not found
     */
    TicketCommentResponse getTicketCommentByUuid(String uuid);

    /**
     * Returns the TicketComment and checked if in the right project and ticket
     *
     * @param projectUuid the uuid of the project
     * @param ticketUuid  the uuid of the ticket
     * @param commentUuid the uuid of the comment
     * @return {@link TicketCommentResponse}
     * @throws TicketCommentNotInTickedException if the comment is not in the ticket
     * @throws TicketNotInProjectException       if the ticket is not in the project
     */
    TicketCommentResponse getTicketCommentByProjectAndTickedAndUuid(String projectUuid,
                                                                    String ticketUuid, String commentUuid);

    /**
     * Returns the added ticketComment
     *
     * @param ticketCommentRequest the ticketComment request of a ticketComment to be added
     * @return {@link TicketCommentResponse}
     * @throws TicketCommentAlreadyExistsException if the ticketComment already exists
     * @throws TicketNotFoundException             if the ticket does not exist
     */
    TicketCommentResponse addTicketComment(TicketCommentRequest ticketCommentRequest);

    /**
     * Returns the added ticketComment
     *
     * @param projectUuid          the uuid of the project
     * @param ticketUuid           the uuid of the ticket
     * @param ticketCommentRequest the ticketComment request of a ticketComment to be added
     * @return {@link TicketCommentResponse}
     * @throws TicketCommentAlreadyExistsException if the ticketComment already exists
     * @throws TicketNotFoundException             if the ticket does not exist
     */
    TicketCommentResponse addTicketCommentByProjectAndTicket(String projectUuid, String ticketUuid,
                                                             TicketCommentRequest ticketCommentRequest);

    /**
     * Returns the updated ticketComment
     *
     * @param uuid          the uuid of the ticketComment
     * @param ticketComment the updated ticketComment
     * @return {@link TicketCommentResponse}
     * @throws TicketCommentNotFoundException if no ticketComment exists with given uuid
     */
    TicketCommentResponse updateTicketComment(String uuid, TicketCommentRequest ticketComment);

    /**
     * Returns the updated ticketComment
     *
     * @param projectUuid          the uuid of the project
     * @param ticketUuid           the uuid of the ticket
     * @param commentId            the uuid of the ticketComment
     * @param ticketCommentRequest the ticketComment request of a ticketComment to be updated
     * @return {@link TicketCommentResponse}
     * @throws TicketCommentNotInTickedException if the comment is not in the ticket
     * @throws TicketNotInProjectException       if the ticket is not in the project
     */
    TicketCommentResponse updateTicketCommentByProjectAndTicket(String projectUuid, String ticketUuid,
                                                                String commentId,
                                                                TicketCommentRequest ticketCommentRequest);

    /**
     * Deletes the ticketComment with the given uuid
     *
     * @param uuid the uuid of the ticketComment to be deleted
     * @throws TicketCommentNotFoundException if no ticketComment found with given uuid
     */
    void deleteTicketCommentByUuid(String uuid);

    /**
     *
     * @param projectUuid the uuid of the project
     * @param ticketUuid  the uuid of the ticket
     * @param commentUuid the uuid of the ticketComment
     * @throws TicketCommentNotInTickedException if the comment is not in the ticket
     * @throws TicketNotInProjectException       if the ticket is not in the project
     */
    void deleteTicketCommentByProjectAndTicket(String projectUuid, String ticketUuid, String commentUuid);

//    /**
//     * Deletes a list of ticketComments
//     *
//     * @param ticketCommentList the list of tickets to be deleted
//     */
//    void deleteTicketList(List<TicketCommentRequest> ticketCommentList);
}
