package bg.sofia.uni.fmi.issuetracker.service.contract.ticket;

import bg.sofia.uni.fmi.issuetracker.dto.input.ticket.CreateTicketDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.ticket.UpdateTicketDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.ticket.TicketDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.exception.InvalidWorkflowTransitionException;
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
     * Retrieves a ticket by its code.
     *
     * @param code the unique code identifier of the ticket
     * @return a {@link TicketDetailsDTO} containing the ticket details
     * @throws TicketNotFoundException if no ticket with the given code exists
     */
    TicketDetailsDTO getTicketByCode(String code);

    /**
     * Retrieves all tickets belonging to a specific project.
     *
     * @param projectUuid the UUID of the project
     * @return a list of {@link TicketDetailsDTO} for all tickets in the project
     * @throws ProjectNotFoundException if the project with the given UUID does not exist
     */
    List<TicketDetailsDTO> getAllTicketsByProject(String projectUuid);

    /**
     * Creates a new ticket.
     *
     * @param dto the {@link CreateTicketDTO} containing ticket creation data
     * @return the created {@link Ticket} entity
     * @throws TicketAlreadyExistsException  if a ticket with the given code already exists
     * @throws UserNotFoundException         if the assignee username does not exist
     * @throws ProjectNotFoundException      if the project does not exist
     * @throws UserNotPartOfProjectException if the assignee is not a member of the project
     */
    Ticket createTicket(CreateTicketDTO dto);

    /**
     * Adds a dependent ticket to a parent ticket.
     *
     * @param parentTicketCode the code of the parent ticket
     * @param dto              the {@link CreateTicketDTO} containing dependent ticket creation data
     * @throws TicketNotFoundException       if the parent ticket does not exist
     * @throws TicketNotInProjectException   if the dependent ticket's project does not match the parent ticket's project
     * @throws TicketAlreadyExistsException  if a ticket with the given code already exists
     * @throws UserNotFoundException         if the assignee username does not exist
     * @throws UserNotPartOfProjectException if the assignee is not a member of the project
     */
    void addDependentTicketToTicket(String parentTicketCode, CreateTicketDTO dto);

    /**
     * Changes the assignee of a ticket.
     *
     * @param ticketCode       the code of the ticket to update
     * @param assigneeUsername the username of the new assignee
     * @throws TicketNotFoundException if the ticket does not exist
     * @throws UserNotFoundException   if the assignee username does not exist
     */
    void changeTicketAssignee(String ticketCode, String assigneeUsername);

    /**
     * Removes the assignee of a ticket.
     *
     * @param ticketCode the code of the ticket to update
     * @throws TicketNotFoundException   if the ticket does not exist
     * @throws UnassignedTicketException if the ticket is currently unassigned
     */
    void removeTicketAssignee(String ticketCode);

    /**
     * Updates ticket information.
     *
     * @param code the code of the ticket to update
     * @param dto  the {@link UpdateTicketDTO} containing updated ticket data
     * @throws TicketNotFoundException            if the ticket does not exist
     * @throws InvalidWorkflowTransitionException if the status transition is not allowed by the workflow
     */
    void updateTicket(String code, UpdateTicketDTO dto);

    /**
     * Deletes a ticket.
     *
     * @param ticketCode the code of the ticket to delete
     * @throws TicketNotFoundException if the ticket does not exist
     */
    void deleteTicket(String ticketCode);
}
