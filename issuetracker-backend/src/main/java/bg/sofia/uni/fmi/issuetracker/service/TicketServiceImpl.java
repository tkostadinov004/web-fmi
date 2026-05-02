package bg.sofia.uni.fmi.issuetracker.service;

import bg.sofia.uni.fmi.issuetracker.dto.ticket.TicketRequest;
import bg.sofia.uni.fmi.issuetracker.dto.ticket.TicketResponse;
import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectDoesNotExistException;
import bg.sofia.uni.fmi.issuetracker.exception.project.UserNotPartOfProjectException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketNotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketNotInProject;
import bg.sofia.uni.fmi.issuetracker.model.project.Project;
import bg.sofia.uni.fmi.issuetracker.model.ticket.Ticket;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketStatus;
import bg.sofia.uni.fmi.issuetracker.repository.ProjectRepository;
import bg.sofia.uni.fmi.issuetracker.repository.ticket.TicketRepository;
import bg.sofia.uni.fmi.issuetracker.service.contract.ticket.TicketService;
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
            new TicketNotFoundException("Ticket with uuid: " + uuid + " not found")));
    }

    @Override
    public List<TicketResponse> getAllTicketsByProjectUuid(String projectUuid) {
        Optional<Project> project = projectRepository.findById(projectUuid);
        if (project.isEmpty()) {
            throw new ProjectDoesNotExistException("Project with uuid: " + projectUuid + " not found");
        }

        return List.of((TicketResponse) ticketRepository.findAllByProject(project.get()));
    }

    @Override
    public Optional<TicketResponse> getTicketByProjectUuidAndTicketUuid(String project_uuid, String ticket_uuid) {
        Optional<Project> project = projectRepository.findById(project_uuid);
        if (project.isEmpty()) {
            throw new ProjectDoesNotExistException("Project with uuid: " + project_uuid + " not found");
        }

        Optional<Ticket> ticket = ticketRepository.findByUuidAndProjectUuid(ticket_uuid, project_uuid);
        if (ticket.isEmpty()) {
            throw new TicketNotFoundException("Ticket with uuid: " + ticket_uuid + " not found");
        }

        return Optional.of(TicketResponse.from(ticket.get()));
    }

    @Override
    public TicketResponse addTicketByProjectUuid(String projectUuid, TicketRequest request) {
        if (ticketRepository.existsById(request.getUuid())) {
            throw new TicketAlreadyExistsException("Ticket with uuid: " + request.getUuid() + " already exists");
        }

        Project project = projectRepository.findById(projectUuid).orElseThrow(() ->
            new ProjectDoesNotExistException("Project with uuid: " + projectUuid + " doesn't exist"));

        if (request.getAssignee() != null && projectRepository.isUserInProject(request.getAssignee(), project)) {
            throw new UserNotPartOfProjectException("User is not part of the project");
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

        Project project = projectRepository.findById(ticket.getProject().getUuid())
            .orElseThrow(() -> new ProjectDoesNotExistException("Project not found"));

        if (ticket.getAssignee() != null && !projectRepository.isUserInProject(ticket.getAssignee(), project)) {
            throw new UserNotPartOfProjectException("User is not part of the project");
        } else if (!project.getTickets().contains(existing)) {
            throw new TicketNotInProject("Ticket with uuid: " + ticket.getUuid() + " not found within project");
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
            new ProjectDoesNotExistException("Project with uuid: " + projectUuid + " doesn't exist"));

        if (ticket.getAssignee() != null && !projectRepository.isUserInProject(ticket.getAssignee(), project)) {
            throw new UserNotPartOfProjectException("User is not part of the project");
        } else if (!project.getTickets().contains(ticket)) {
            throw new TicketNotFoundException("Ticket with uuid: " + ticket.getUuid() + " not found within project");
        }

        ticketRepository.delete(ticket);
    }

//    @Override
//    public void deleteTicketList(List<Ticket> ticketList) {
//        ticketRepository.deleteAll(ticketList);
//    }

    private Ticket getTicket(String uuid) {
        return ticketRepository.findByUuid(uuid).orElseThrow(() ->
            new TicketNotFoundException("Ticket with uuid: " + uuid + " not found"));
    }
}
