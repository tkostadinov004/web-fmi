package bg.sofia.uni.fmi.issuetracker.service;

import bg.sofia.uni.fmi.issuetracker.dto.ticket.TicketRequest;
import bg.sofia.uni.fmi.issuetracker.dto.ticket.TicketResponse;
import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectDoesNotExistException;
import bg.sofia.uni.fmi.issuetracker.exception.project.UserNotPartOfProjectException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketNotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketNotInProjectException;
import bg.sofia.uni.fmi.issuetracker.model.project.Project;
import bg.sofia.uni.fmi.issuetracker.model.ticket.Ticket;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketPriority;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketStatus;
import bg.sofia.uni.fmi.issuetracker.repository.ProjectRepository;
import bg.sofia.uni.fmi.issuetracker.repository.ticket.TicketRepository;
import bg.sofia.uni.fmi.issuetracker.service.contract.ticket.TicketService;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final ProjectRepository projectRepository;

    public TicketServiceImpl(TicketRepository ticketRepository, ProjectRepository projectRepository) {
        this.ticketRepository = ticketRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public TicketResponse getTicketByUuid(String uuid) {
        return TicketResponse.from(ticketRepository.findById(uuid).orElseThrow(() ->
            new TicketNotFoundException(ExceptionMessages.Ticket.ticketNotFound(uuid))));
    }

    @Override
    public List<TicketResponse> getAllTicketsByProjectUuid(String projectUuid) {
        Project project = checkIfProjectExists(projectUuid);

        return List.of((TicketResponse) ticketRepository.findAllByProject(project));
    }

    @Override
    public List<TicketResponse> getAllTicketsByProjectUuidStatusPriorityAndAssigneeUsername(String projectUuid,
                                                                                            TicketStatus status,
                                                                                            TicketPriority priority,
                                                                                            String assigneeUsername) {
        checkIfProjectExists(projectUuid);
        List<Ticket> result = ticketRepository.
            findByTicketStatusAndTicketPriorityAndAssigneeUsername(status, priority, assigneeUsername);

        return result.stream()
            .map(TicketResponse::from)
            .toList();
    }

    @Override
    public Optional<TicketResponse> getTicketByProjectUuidAndTicketUuid(String project_uuid, String ticket_uuid) {
        checkIfProjectExists(project_uuid);

        Optional<Ticket> ticket = ticketRepository.findByUuidAndProjectUuid(ticket_uuid, project_uuid);
        if (ticket.isEmpty()) {
            throw new TicketNotFoundException(ExceptionMessages.Ticket.ticketNotFound(ticket_uuid));
        }

        return Optional.of(TicketResponse.from(ticket.get()));
    }

    @Override
    public TicketResponse addTicketByProjectUuid(String projectUuid, TicketRequest request) {
        if (ticketRepository.existsById(request.getUuid())) {
            throw new TicketAlreadyExistsException(ExceptionMessages.Ticket.ticketAlreadyExists(request.getUuid()));
        }

        Project project = checkIfProjectExists(projectUuid);

        if (request.getAssignee() != null && projectRepository.isUserInProject(request.getAssignee(), project)) {
            throw new UserNotPartOfProjectException(
                ExceptionMessages.Project.userNotInProject(request.getAssignee().getUsername(), projectUuid));
        }

        if (request.getTicketStatus() == null) {
            request.setTicketStatus(TicketStatus.TO_DO);
        }

        Ticket toAdd = new Ticket(request.getTitle(), request.getDescription(), request.getSprint(),
            request.getTicketPriority(), request.getTicketStatus(), request.getDueDate(), request.getProject(),
            request.getAssignee(), request.getDependentTicketUuids());

        ticketRepository.save(toAdd);

        return TicketResponse.from(toAdd);
    }

    @Override
    public TicketResponse updateTicketByProjectUuid(String projectUuid, String uuid, TicketRequest ticket) {

        Ticket existing = getTicket(uuid);

        Project project = checkIfProjectExists(projectUuid);

        if (ticket.getAssignee() != null && !projectRepository.isUserInProject(ticket.getAssignee(), project)) {
            throw new UserNotPartOfProjectException(
                ExceptionMessages.Project.userNotInProject(ticket.getAssignee().getUsername(), projectUuid));
        } else if (!project.getTickets().contains(existing)) {
            throw new TicketNotInProjectException(
                ExceptionMessages.Ticket.ticketNotInProject(ticket.getUuid(), projectUuid));
        }

        existing.setTitle(ticket.getTitle());
        existing.setDescription(ticket.getDescription());
        existing.setSprint(ticket.getSprint());
        existing.setTicketPriority(ticket.getTicketPriority());
        existing.setTicketStatus(ticket.getTicketStatus());
        // existing.setProject(project); ???
        existing.setAssignee(ticket.getAssignee());
        existing.setUpdateDate();

        return TicketResponse.from(ticketRepository.save(existing));
    }

    @Override
    public void deleteTicketByProjectUuid(String projectUuid, String ticketUuid) {
        Ticket ticket = getTicket(ticketUuid);
        Project project = projectRepository.findById(projectUuid).orElseThrow(() ->
            new ProjectDoesNotExistException(ExceptionMessages.Project.projectNotFound(projectUuid)));

        if (ticket.getAssignee() != null && !projectRepository.isUserInProject(ticket.getAssignee(), project)) {
            throw new UserNotPartOfProjectException(
                ExceptionMessages.Project.userNotInProject(ticket.getAssignee().getUsername(), projectUuid));
        } else if (!project.getTickets().contains(ticket)) {
            throw new TicketNotFoundException(ExceptionMessages.Ticket.ticketNotFound(ticket.getUuid()));
        }

        ticketRepository.delete(ticket);
    }

    private Ticket getTicket(String uuid) {
        return ticketRepository.findByUuid(uuid).orElseThrow(() ->
            new TicketNotFoundException(ExceptionMessages.Ticket.ticketNotFound(uuid)));
    }

    private Project checkIfProjectExists(String projectUuid) {
        Optional<Project> project = projectRepository.findById(projectUuid);
        if (project.isEmpty()) {
            throw new ProjectDoesNotExistException(ExceptionMessages.Project.projectNotFound(projectUuid));
        }

        return project.get();
    }
}
