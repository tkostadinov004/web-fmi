package bg.sofia.uni.fmi.issuetracker.service;

import bg.sofia.uni.fmi.issuetracker.auditlog.AuditLog;
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
import bg.sofia.uni.fmi.issuetracker.exception.ticket.UnassignedTicketException;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserNotFoundException;
import bg.sofia.uni.fmi.issuetracker.model.User;
import bg.sofia.uni.fmi.issuetracker.model.auditlog.AuditLogType;
import bg.sofia.uni.fmi.issuetracker.model.project.Project;
import bg.sofia.uni.fmi.issuetracker.model.ticket.Ticket;
import bg.sofia.uni.fmi.issuetracker.repository.ProjectRepository;
import bg.sofia.uni.fmi.issuetracker.repository.TicketRepository;
import bg.sofia.uni.fmi.issuetracker.repository.UserRepository;
import bg.sofia.uni.fmi.issuetracker.repository.neo.WorkflowRepository;
import bg.sofia.uni.fmi.issuetracker.service.contract.TicketService;
import bg.sofia.uni.fmi.issuetracker.service.mapper.TicketMapper;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService {
    private final TicketRepository ticketRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TicketMapper ticketMapper;
    private final WorkflowRepository workflowRepository;

    public TicketServiceImpl(TicketRepository ticketRepository, ProjectRepository projectRepository,
                             UserRepository userRepository, TicketMapper ticketMapper, WorkflowRepository workflowRepository) {
        this.ticketRepository = ticketRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.ticketMapper = ticketMapper;
        this.workflowRepository = workflowRepository;
    }

    @Override
    public TicketDetailsDTO getTicketByCode(String projectId, String code) {
        Ticket ticket = getTicket(projectId, code);

        return TicketDetailsDTO.from(ticket);
    }

    @Override
    public List<TicketDetailsDTO> getAllTicketsByProject(String projectUuid) {
        Project project = fetchProject(projectUuid);

        return project
                .getTickets()
                .stream()
                .map(TicketDetailsDTO::from)
                .toList();
    }

    @Override
    @AuditLog(message = "Created ticket", type = AuditLogType.CREATE)
    public Ticket createTicket(String projectId, CreateTicketDTO dto) {
        Optional<User> assignee = Optional.empty();
        if (dto.assigneeUsername() != null) {
            assignee = userRepository.findById(dto.assigneeUsername());
            if (assignee.isEmpty()) {
                throw new UserNotFoundException(ExceptionMessages.User.userNotFound(dto.assigneeUsername()));
            }
        }

        Project project = fetchProject(projectId);
        if (assignee.isPresent() && !projectRepository.isUserInProject(assignee.get(), project)) {
            throw new UserNotPartOfProjectException(
                    ExceptionMessages.Project.userNotInProject(dto.assigneeUsername(), projectId));
        }
        if (ticketRepository.existsById_CodeAndProject(dto.code(), project)) {
            throw new TicketAlreadyExistsException(ExceptionMessages.Ticket.ticketAlreadyExists(dto.code(), project.getUuid()));
        }
        List<String> workflowStatuses = workflowRepository.getStatuses(project.getUuid());
        if (!workflowStatuses.contains(dto.ticketStatus())) {
            throw new InvalidWorkflowException(ExceptionMessages.Ticket.invalidStatus(workflowStatuses));
        }

        Ticket ticket = new Ticket(dto.code(), dto.title(), dto.description(), dto.ticketPriority(), dto.ticketStatus(),
                dto.dueDate(), project, assignee.orElse(null), List.of());

        return ticketRepository.save(ticket);
    }

    @Override
    public List<DependentTicketDTO> getDependentTickets(String projectId, String code) {
        Ticket ticket = getTicket(projectId, code);

        return ticket
                .getDependentTickets()
                .stream()
                .map(t -> new DependentTicketDTO(t.getCode(), t.getTitle(), t.getDescription(), t.getTicketStatus()))
                .toList();
    }

    @Override
    @Transactional
    public void addDependentTicketToTicket(String parentTicketProjectId, String parentTicketCode, CreateTicketDTO ticket) {
        Ticket parentTicket = getTicket(parentTicketProjectId, parentTicketCode);
        Ticket dependentTicket = createTicket(parentTicketProjectId, ticket);

        parentTicket.addDependentTicket(dependentTicket);
        ticketRepository.save(parentTicket);
    }

    @Override
    public void changeTicketAssignee(String projectId, String ticketCode, String assigneeUsername) {
        Ticket ticket = getTicket(projectId, ticketCode);
        Optional<User> assignee = userRepository.findById(assigneeUsername);
        if (assignee.isEmpty()) {
            throw new UserNotFoundException(ExceptionMessages.User.userNotFound(assigneeUsername));
        }

        ticket.setAssignee(assignee.get());
        ticketRepository.save(ticket);
    }

    @Override
    public void removeTicketAssignee(String projectId, String ticketCode) {
        Ticket ticket = getTicket(projectId, ticketCode);
        if (ticket.getAssignee() == null) {
            throw new UnassignedTicketException(ExceptionMessages.Ticket.unassignedTicket(ticketCode));
        }

        ticket.setAssignee(null);
        ticketRepository.save(ticket);
    }

    @Override
    @Transactional
    public void updateTicket(String projectId, String code, UpdateTicketDTO dto) {
        Ticket ticket = getTicket(projectId, code);

        if (dto.ticketStatus() != null && !workflowRepository.isTransitionPossible(ticket.getProject().getUuid(), ticket.getTicketStatus(), dto.ticketStatus())) {
            throw new InvalidWorkflowTransitionException(
                    ExceptionMessages.Workflow.invalidStatus(ticket.getTicketStatus(), dto.ticketStatus()));
        }
        ticketMapper.patchTicketFromDTO(dto, ticket);
        ticketRepository.save(ticket);
    }

    @Override
    @Transactional
    public void deleteTicket(String projectId, String ticketCode) {
        Ticket ticket = getTicket(projectId, ticketCode);

        ticketRepository.deleteAll(ticket.getDependentTickets());
        ticketRepository.delete(ticket);
    }

    private Project fetchProject(String projectUuid) {
        Optional<Project> project = projectRepository.findById(projectUuid);
        if (project.isEmpty()) {
            throw new ProjectNotFoundException(ExceptionMessages.Project.projectNotFound(projectUuid));
        }

        return project.get();
    }

    private Ticket getTicket(String projectId, String code) {
        Project project = fetchProject(projectId);

        Optional<Ticket> ticket = ticketRepository.findById_CodeAndProject(code, project);
        if (ticket.isEmpty()) {
            throw new TicketNotFoundException(ExceptionMessages.Ticket.ticketNotFound(code, projectId));
        }
        return ticket.get();
    }
}
