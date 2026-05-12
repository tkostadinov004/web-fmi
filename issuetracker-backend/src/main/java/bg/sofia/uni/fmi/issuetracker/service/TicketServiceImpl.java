package bg.sofia.uni.fmi.issuetracker.service;

import bg.sofia.uni.fmi.issuetracker.dto.input.ticket.CreateTicketDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.ticket.UpdateTicketDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.ticket.TicketDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.exception.InvalidWorkflowTransitionException;
import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectNotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.project.UserNotPartOfProjectException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketNotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketNotInProjectException;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserNotFoundException;
import bg.sofia.uni.fmi.issuetracker.model.User;
import bg.sofia.uni.fmi.issuetracker.model.project.Project;
import bg.sofia.uni.fmi.issuetracker.model.ticket.Ticket;
import bg.sofia.uni.fmi.issuetracker.repository.ProjectRepository;
import bg.sofia.uni.fmi.issuetracker.repository.UserRepository;
import bg.sofia.uni.fmi.issuetracker.repository.ticket.TicketRepository;
import bg.sofia.uni.fmi.issuetracker.service.contract.ticket.TicketService;
import bg.sofia.uni.fmi.issuetracker.service.mapper.TicketMapper;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import bg.sofia.uni.fmi.issuetracker.workflow.TicketWorkflow;
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

    public TicketServiceImpl(TicketRepository ticketRepository, ProjectRepository projectRepository,
                             UserRepository userRepository, TicketMapper ticketMapper) {
        this.ticketRepository = ticketRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.ticketMapper = ticketMapper;
    }

    @Override
    public TicketDetailsDTO getTicketByCode(String code) {
        Ticket ticket = getTicket(code);

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
    public Ticket createTicket(CreateTicketDTO dto) {
        if (ticketRepository.existsById(dto.code())) {
            throw new TicketAlreadyExistsException(ExceptionMessages.Ticket.ticketAlreadyExists(dto.code()));
        }

        Optional<User> assignee = Optional.empty();
        if (dto.assigneeUsername() != null) {
            assignee = userRepository.findById(dto.assigneeUsername());
            if (assignee.isEmpty()) {
                throw new UserNotFoundException(ExceptionMessages.User.userNotFound(dto.assigneeUsername()));
            }
        }

        Project project = fetchProject(dto.projectUuid());
        if (assignee.isPresent() && !projectRepository.isUserInProject(assignee.get(), project)) {
            throw new UserNotPartOfProjectException(
                    ExceptionMessages.Project.userNotInProject(dto.assigneeUsername(), dto.projectUuid()));
        }

        Ticket ticket = new Ticket(dto.code(), dto.title(), dto.description(), dto.ticketPriority(), dto.ticketStatus(),
                dto.dueDate(), project, assignee.orElse(null), List.of());

        return ticketRepository.save(ticket);
    }

    @Override
    @Transactional
    public void addDependentTicketToTicket(String parentTicketCode, CreateTicketDTO ticket) {
        Ticket parentTicket = getTicket(parentTicketCode);
        if (!parentTicket.getProject().getUuid().equals(ticket.projectUuid())) {
            throw new TicketNotInProjectException(
                    ExceptionMessages.Ticket.ticketProjectMismatch(ticket.code(), parentTicketCode));
        }

        Ticket dependentTicket = createTicket(ticket);

        parentTicket.addDependentTicket(dependentTicket);
        ticketRepository.save(parentTicket);
    }

    @Override
    public void changeTicketAssignee(String ticketCode, String assigneeUsername) {
        Ticket ticket = getTicket(ticketCode);
        if (assigneeUsername == null) {
            ticket.setAssignee(null);
            ticketRepository.save(ticket);
            return;
        }

        Optional<User> assignee = userRepository.findById(assigneeUsername);
        if (assignee.isEmpty()) {
            throw new UserNotFoundException(ExceptionMessages.User.userNotFound(assigneeUsername));
        }

        ticket.setAssignee(assignee.get());
        ticketRepository.save(ticket);
    }

    @Override
    @Transactional
    public void updateTicket(String code, UpdateTicketDTO dto) {
        Ticket ticket = getTicket(code);

        if (dto.ticketStatus() != null && !TicketWorkflow.isTransitionAllowed(ticket.getTicketStatus(), dto.ticketStatus())) {
            throw new InvalidWorkflowTransitionException(
                    ExceptionMessages.Workflow.invalidStatus(ticket.getTicketStatus().toString(), dto.ticketStatus().toString()));
        }
        ticketMapper.patchTicketFromDTO(dto, ticket);
        ticketRepository.save(ticket);
    }

    @Override
    @Transactional
    public void deleteTicket(String ticketCode) {
        Ticket ticket = getTicket(ticketCode);

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

    private Ticket getTicket(String code) {
        Optional<Ticket> ticket = ticketRepository.findById(code);
        if (ticket.isEmpty()) {
            throw new TicketNotFoundException(ExceptionMessages.Ticket.ticketNotFound(code));
        }
        return ticket.get();
    }
}
