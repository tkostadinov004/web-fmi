package bg.sofia.uni.fmi.issuetracker.service.contract;

import bg.sofia.uni.fmi.issuetracker.dto.input.ticket.CreateTicketDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.ticket.UpdateTicketDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.ticket.DependentTicketDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.ticket.TicketDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.exception.InvalidWorkflowTransitionException;
import bg.sofia.uni.fmi.issuetracker.exception.project.InvalidWorkflowException;
import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectNotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.project.UserNotPartOfProjectException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketNotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketNotInProjectException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.UnassignedTicketException;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserNotFoundException;
import bg.sofia.uni.fmi.issuetracker.model.ticket.Ticket;

import java.util.List;

public interface TicketService {
    /**
     * Retrieves a ticket by project UUID and its code within that project.
     *
     * @param projectId the UUID of the project the ticket belongs to
     * @param code      the unique code identifier of the ticket within the project
     * @return a {@link TicketDetailsDTO} containing the ticket details
     * @throws TicketNotFoundException  if no ticket with the given code exists in the project
     * @throws ProjectNotFoundException if the project with the given UUID does not exist
     */
    TicketDetailsDTO getTicketByCode(String projectId, String code);

    /**
     * Retrieves all tickets belonging to a specific project.
     *
     * @param projectUuid the UUID of the project
     * @return a list of {@link TicketDetailsDTO} for all tickets in the project
     * @throws ProjectNotFoundException if the project with the given UUID does not exist
     */
    List<TicketDetailsDTO> getAllTicketsByProject(String projectUuid);

    /**
     * Creates a new ticket inside the specified project.
     * Validates assignee existence and that the assignee is a member of the project.
     * Also validates that the provided status is part of the project's workflow.
     *
     * @param projectId the UUID of the project in which to create the ticket
     * @param dto       the {@link CreateTicketDTO} containing ticket creation data
     * @return the created {@link Ticket} entity
     * @throws TicketAlreadyExistsException  if a ticket with the given code already exists in the project
     * @throws UserNotFoundException         if the assignee username does not exist
     * @throws ProjectNotFoundException      if the project does not exist
     * @throws UserNotPartOfProjectException if the assignee is not a member of the project
     * @throws InvalidWorkflowException      if the supplied ticket status is not valid for the project
     */
    Ticket createTicket(String projectId, CreateTicketDTO dto);

    /**
     * Returns all the dependent tickets of a given ticket.
     *
     * @param projectId the UUID of the project
     * @param code      the code of the parent ticket
     */
    List<DependentTicketDTO> getDependentTickets(String projectId, String code);

    /**
     * Adds a dependent ticket to a parent ticket. Both tickets must belong
     * to the same project; the dependent ticket is created and then linked to the parent.
     *
     * @param parentTicketProjectId the project UUID of the parent ticket
     * @param parentTicketCode      the code of the parent ticket
     * @param dto                   the {@link CreateTicketDTO} containing dependent ticket creation data
     * @throws TicketNotFoundException       if the parent ticket does not exist
     * @throws TicketNotInProjectException   if the dependent ticket's project does not match the parent ticket's project
     * @throws TicketAlreadyExistsException  if a ticket with the given code already exists
     * @throws UserNotFoundException         if the assignee username does not exist
     * @throws UserNotPartOfProjectException if the assignee is not a member of the project
     * @throws ProjectNotFoundException      if the parent ticket's project does not exist
     */
    void addDependentTicketToTicket(String parentTicketProjectId, String parentTicketCode, CreateTicketDTO dto);

    /**
     * Changes the assignee of a ticket within the given project.
     *
     * @param projectId        the project UUID
     * @param ticketCode       the code of the ticket to update
     * @param assigneeUsername the username of the new assignee
     * @throws TicketNotFoundException  if the ticket does not exist
     * @throws UserNotFoundException    if the assignee username does not exist
     * @throws ProjectNotFoundException if the project does not exist
     */
    void changeTicketAssignee(String projectId, String ticketCode, String assigneeUsername);

    /**
     * Removes the assignee of a ticket within the given project.
     *
     * @param projectId  the project UUID
     * @param ticketCode the code of the ticket to update
     * @throws TicketNotFoundException   if the ticket does not exist
     * @throws UnassignedTicketException if the ticket is currently unassigned
     * @throws ProjectNotFoundException  if the project does not exist
     */
    void removeTicketAssignee(String projectId, String ticketCode);

    /**
     * Updates ticket information within the specified project.
     *
     * @param projectId the project UUID
     * @param code      the code of the ticket to update
     * @param dto       the {@link UpdateTicketDTO} containing updated ticket data
     * @throws TicketNotFoundException            if the ticket does not exist
     * @throws InvalidWorkflowTransitionException if the status transition is not allowed by the workflow
     * @throws ProjectNotFoundException           if the project does not exist
     */
    void updateTicket(String projectId, String code, UpdateTicketDTO dto);

    /**
     * Deletes a ticket from the specified project.
     *
     * @param projectId  the project UUID
     * @param ticketCode the code of the ticket to delete
     * @throws TicketNotFoundException  if the ticket does not exist
     * @throws ProjectNotFoundException if the project does not exist
     */
    void deleteTicket(String projectId, String ticketCode);
}
