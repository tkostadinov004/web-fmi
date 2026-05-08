package bg.sofia.uni.fmi.issuetracker.service;

import bg.sofia.uni.fmi.issuetracker.dto.input.ticket.CreateTicketCommentDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.ticket.UpdateTicketCommentDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.ticket.TicketCommentDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.exception.OwnershipMismatchException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketCommentNotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketNotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserNotFoundException;
import bg.sofia.uni.fmi.issuetracker.model.User;
import bg.sofia.uni.fmi.issuetracker.model.ticket.Ticket;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketComment;
import bg.sofia.uni.fmi.issuetracker.repository.UserRepository;
import bg.sofia.uni.fmi.issuetracker.repository.ticket.TicketCommentRepository;
import bg.sofia.uni.fmi.issuetracker.repository.ticket.TicketRepository;
import bg.sofia.uni.fmi.issuetracker.service.contract.ticket.TicketCommentService;
import bg.sofia.uni.fmi.issuetracker.service.mapper.TicketCommentMapper;
import bg.sofia.uni.fmi.issuetracker.utils.Constants;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TicketCommentServiceImpl implements TicketCommentService {
    private final TicketCommentRepository ticketCommentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketCommentMapper mapper;

    public TicketCommentServiceImpl(TicketCommentRepository ticketCommentRepo, TicketRepository ticketRepo, UserRepository userRepository, TicketCommentMapper mapper) {
        this.ticketCommentRepository = ticketCommentRepo;
        this.ticketRepository = ticketRepo;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public Page<TicketCommentDetailsDTO> getAllCommentsForTicket(String ticketCode, int pageNumber, int pageSize) {
        pageNumber = pageNumber <= 0 ? Integer.parseInt(Constants.DEFAULT_PAGE_NUMBER) : pageNumber;
        pageSize = pageSize <= 0 ? Integer.parseInt(Constants.DEFAULT_PAGE_SIZE) : pageSize;

        Optional<Ticket> ticket = ticketRepository.findById(ticketCode);
        if (ticket.isEmpty()) {
            throw new TicketNotFoundException(ExceptionMessages.Ticket.ticketNotFound(ticketCode));
        }

        Pageable pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        Page<TicketComment> page = ticketCommentRepository.findAllByTicket(ticket.get(), pageRequest);

        return page.map(TicketCommentDetailsDTO::from);
    }

    @Override
    public TicketCommentDetailsDTO getTicketComment(String commentUuid) {
        Optional<TicketComment> ticketComment = ticketCommentRepository.findById(commentUuid);
        if (ticketComment.isEmpty()) {
            throw new TicketCommentNotFoundException(ExceptionMessages.TicketComment.ticketCommentNotFound(commentUuid));
        }

        return TicketCommentDetailsDTO.from(ticketComment.get());
    }

    @Override
    public void addTicketComment(String authorUsername, String ticketCode, CreateTicketCommentDTO dto) {
        Optional<User> author = userRepository.findById(authorUsername);
        if (author.isEmpty()) {
            throw new UserNotFoundException(ExceptionMessages.User.userNotFound(authorUsername));
        }

        Optional<Ticket> ticket = ticketRepository.findById(ticketCode);
        if (ticket.isEmpty()) {
            throw new TicketNotFoundException(ExceptionMessages.Ticket.ticketNotFound(ticketCode));
        }

        TicketComment comment = new TicketComment(ticket.get(), author.get(), dto.content(), LocalDateTime.now());
        ticketCommentRepository.save(comment);
    }

    @Override
    @Transactional
    public void updateTicketComment(String commentUuid, UpdateTicketCommentDTO dto, String username) {
        Optional<TicketComment> ticketComment = ticketCommentRepository.findById(commentUuid);
        if (ticketComment.isEmpty()) {
            throw new TicketCommentNotFoundException(ExceptionMessages.TicketComment.ticketCommentNotFound(commentUuid));
        }
        if (!ticketComment.get().getAuthor().getUsername().equals(username)) {
            throw new OwnershipMismatchException(ExceptionMessages.TicketComment.allowedToModifyOnlyOwnComments());
        }

        mapper.patchTicketCommentFromDTO(dto, ticketComment.get());
        ticketCommentRepository.save(ticketComment.get());
    }

    @Override
    public void deleteTicketComment(String commentUuid, String username) {
        Optional<TicketComment> ticketComment = ticketCommentRepository.findById(commentUuid);
        if (ticketComment.isEmpty()) {
            throw new TicketCommentNotFoundException(ExceptionMessages.TicketComment.ticketCommentNotFound(commentUuid));
        }
        if (!ticketComment.get().getAuthor().getUsername().equals(username)) {
            throw new OwnershipMismatchException(ExceptionMessages.TicketComment.allowedToDeleteOnlyOwnComments());
        }

        ticketCommentRepository.delete(ticketComment.get());
    }
}
