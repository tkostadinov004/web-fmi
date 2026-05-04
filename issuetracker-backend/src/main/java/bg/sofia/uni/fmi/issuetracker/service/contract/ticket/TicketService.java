package bg.sofia.uni.fmi.issuetracker.service.contract.ticket;

import bg.sofia.uni.fmi.issuetracker.dto.ticket.TicketRequest;
import bg.sofia.uni.fmi.issuetracker.dto.ticket.TicketResponse;
import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectDoesNotExistException;
import bg.sofia.uni.fmi.issuetracker.exception.project.UserNotPartOfProjectException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketNotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketNotInProjectException;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketPriority;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketStatus;

import java.util.List;
import java.util.Optional;

public interface TicketService {

    /**
     * Returns a ticket by its uuid.
     *
     * @param uuid the uuid to be searched by
     * @return {@link TicketResponse}
     * @throws TicketNotFoundException if no Ticket found with the uuid
     */
    TicketResponse getTicketByUuid(String uuid);

    /**
     * Returns all tickets of a project
     *
     * @param projectUuid the uuid of the project
     * @return {@link List<TicketResponse>}
     * @throws ProjectDoesNotExistException if no project with the given uuid exists
     */
    List<TicketResponse> getAllTicketsByProjectUuid(String projectUuid);

    /**
     * Returns all tickets filtered by status, priority and assigneeUsername ( all not required )
     *
     * @param projectUuid      the uuid of the project
     * @param status           the status of the ticket
     * @param priority         the priority of the ticket
     * @param assigneeUsername the username of the assigned User
     * @return {@link List<TicketResponse>}
     * @throws ProjectDoesNotExistException if no project with the given uuid exists
     */
    List<TicketResponse> getAllTicketsByProjectUuidStatusPriorityAndAssigneeUsername(
        String projectUuid, TicketStatus status, TicketPriority priority, String assigneeUsername);

    /**
     * Returns ticket by project uuid and ticket uuid
     *
     * @param project_uuid the uuid of the project
     * @param ticket_uuid  the uuid of the ticket
     * @return {@link Optional<TicketResponse>}
     * @throws ProjectDoesNotExistException if no project with the given uuid exists
     * @throws TicketNotFoundException      if no Ticket found with the uuid
     */
    Optional<TicketResponse> getTicketByProjectUuidAndTicketUuid(String project_uuid, String ticket_uuid);

    /**
     * Returns the added ticket in the project
     *
     * @param ticket      the ticket request of a ticket to be added
     * @param projectUuid the project uuid
     * @return {@link TicketResponse}
     * @throws TicketAlreadyExistsException if the ticket already exists
     * @throws ProjectDoesNotExistException if no project with the given uuid exists
     */
    TicketResponse addTicketByProjectUuid(String projectUuid, TicketRequest ticket);

    /**
     * Returns the updated ticket
     *
     * @param uuid        the uuid of the ticket
     * @param ticket      the updated ticket request
     * @param projectUuid the project uuid
     * @return {@link TicketResponse}
     * @throws TicketNotInProjectException   if no Ticket found within the project
     * @throws ProjectDoesNotExistException  if no project with the given uuid exists
     * @throws UserNotPartOfProjectException if the user is not part of the project but wants to update
     */
    TicketResponse updateTicketByProjectUuid(String projectUuid, String uuid, TicketRequest ticket);

    /**
     * Deletes the ticket with the uuid
     *
     * @param projectUuid the uuid of the project
     * @param ticketUuid  the uuid of the ticket
     * @throws TicketNotFoundException       if not Ticket found with the uuid
     * @throws ProjectDoesNotExistException  if no project with the given uuid exists
     * @throws UserNotPartOfProjectException if the user is not part of the project but wants to update
     */
    void deleteTicketByProjectUuid(String projectUuid, String ticketUuid);

//    /**
//     * Deletes a list of tickets
//     *
//     * @param ticketList the list of tickets
//     */
//    void deleteTicketList(List<Ticket> ticketList);
}
