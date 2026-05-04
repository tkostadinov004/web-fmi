package bg.sofia.uni.fmi.issuetracker.service;

import bg.sofia.uni.fmi.issuetracker.dto.ticket.TicketCommentRequest;
import bg.sofia.uni.fmi.issuetracker.dto.ticket.TicketCommentResponse;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketCommentAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketCommentNotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketCommentNotInTickedException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketNotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketNotInProjectException;
import bg.sofia.uni.fmi.issuetracker.model.ticket.Ticket;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketComment;
import bg.sofia.uni.fmi.issuetracker.repository.ticket.TicketCommentRepository;
import bg.sofia.uni.fmi.issuetracker.repository.ticket.TicketRepository;
import bg.sofia.uni.fmi.issuetracker.service.contract.ticket.TicketCommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TicketCommentServiceImpl implements TicketCommentService {

    private final TicketCommentRepository ticketCommentRepository;
    private final TicketRepository ticketRepository;

    public TicketCommentServiceImpl(TicketCommentRepository ticketCommentRepo, TicketRepository ticketRepo) {
        this.ticketCommentRepository = ticketCommentRepo;
        this.ticketRepository = ticketRepo;
    }


    @Override
    public List<TicketCommentResponse> getAllTicketCommentsByProjectAndTicket(String projectUuid, String ticketUuid) {
        checkIfTicketInProjectByItsUuid(ticketUuid, projectUuid);

        return ticketCommentRepository.getAllByTicketUuid(ticketUuid).stream()
            .map(TicketCommentResponse::from)
            .toList();
    }

    @Override
    public TicketCommentResponse getTicketCommentByUuid(String uuid) {
        return TicketCommentResponse.from(ticketCommentRepository.findByUuid(uuid).orElseThrow(() ->
            new TicketCommentNotFoundException("Ticket comment with uuid: " + uuid + " not found")));
    }

    @Override
    public TicketCommentResponse getTicketCommentByProjectAndTickedAndUuid(String projectUuid, String ticketUuid,
                                                                           String commentUuid) {
        checkIfTicketInProjectByItsUuid(ticketUuid, projectUuid);
        checkIfTicketCommentInTicketByItsUuid(commentUuid, ticketUuid);

        return getTicketCommentByUuid(commentUuid);
    }

    @Override
    public TicketCommentResponse addTicketComment(TicketCommentRequest request) {

        if (ticketCommentRepository.findByUuid(request.getUuid()).isPresent()) {
            throw new TicketCommentAlreadyExistsException(
                "Ticket comment with uuid: " + request.getUuid() + " already exists");
        }

        Ticket ticket = ticketRepository.findById(request.getTicket().getUuid())
            .orElseThrow(() -> new TicketNotFoundException("Ticket not found"));

        TicketComment comment = new TicketComment(request.getUuid(), request.getTicket(), request.getAuthor(),
            request.getContent(), request.getCreateDate());

        return TicketCommentResponse.from(ticketCommentRepository.save(comment));
    }

    @Override
    public TicketCommentResponse addTicketCommentByProjectAndTicket(String projectUuid, String ticketUuid,
                                                                    TicketCommentRequest ticketCommentRequest) {
        checkIfTicketInProjectByItsUuid(ticketUuid, projectUuid);

        return addTicketComment(ticketCommentRequest);
    }

    @Override
    public TicketCommentResponse updateTicketComment(String uuid, TicketCommentRequest ticketComment) {

        TicketComment existing = getTicketComment(uuid);

        // This should be all that can be changed ???
        existing.setContent(ticketComment.getContent());

        return TicketCommentResponse.from(ticketCommentRepository.save(existing));
    }

    @Override
    public TicketCommentResponse updateTicketCommentByProjectAndTicket(String projectUuid, String ticketUuid,
                                                                       String commentId,
                                                                       TicketCommentRequest ticketCommentRequest) {
        checkIfTicketInProjectByItsUuid(ticketUuid, projectUuid);
        checkIfTicketCommentInTicketByItsUuid(commentId, ticketUuid);

        return updateTicketComment(commentId, ticketCommentRequest);
    }

    @Override
    public void deleteTicketCommentByUuid(String uuid) {
        TicketComment ticketComment = getTicketComment(uuid);
        ticketCommentRepository.delete(ticketComment);
    }

    @Override
    public void deleteTicketCommentByProjectAndTicket(String projectUuid, String ticketUuid, String commentUuid) {
        checkIfTicketInProjectByItsUuid(ticketUuid, projectUuid);
        checkIfTicketCommentInTicketByItsUuid(commentUuid, ticketUuid);

        deleteTicketCommentByUuid(commentUuid);
    }

//    @Override
//    public void deleteTicketList(List<TicketCommentRequest> requests) {
//        ticketCommentRepository.deleteAll(requests.stream()
//            .map(request -> ticketCommentRepository.findByUuid(request.getUuid()).get())
//            .toList());
//    }

    private TicketComment getTicketComment(String uuid) {
        return ticketCommentRepository.findByUuid(uuid).orElseThrow(() ->
            new TicketCommentNotFoundException("Ticket comment with uuid: " + uuid + " not found"));
    }

    // To do after making the project oriented structures
//    private void checkIfProjectExists(String projectUuid) {
//
//    }

    private void checkIfTicketCommentInTicketByItsUuid(String ticketCommentUuid, String ticketUuid) {
        if (!ticketCommentRepository.findByUuid(ticketCommentUuid).get().getTicket().getUuid().equals(ticketUuid)) {
            throw new TicketCommentNotInTickedException(
                "Ticket comment with uuid: " + ticketCommentUuid + " not found within the ticket");
        }
    }

    private void checkIfTicketInProjectByItsUuid(String ticketUuid, String projectUuid) {
        if (!ticketRepository.findByUuid(ticketUuid).get().getProject().getUuid().equals(projectUuid)) {
            throw new TicketNotInProjectException("Ticket with uuid: " + ticketUuid + " not found within the project");
        }
    }
}
