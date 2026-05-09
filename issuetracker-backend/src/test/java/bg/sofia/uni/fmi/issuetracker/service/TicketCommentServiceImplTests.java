package bg.sofia.uni.fmi.issuetracker.service;

import bg.sofia.uni.fmi.issuetracker.dto.input.ticket.CreateTicketCommentDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.ticket.UpdateTicketCommentDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.ticket.TicketCommentDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.exception.OwnershipMismatchException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketCommentNotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketNotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserNotFoundException;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketComment;
import bg.sofia.uni.fmi.issuetracker.repository.UserRepository;
import bg.sofia.uni.fmi.issuetracker.repository.ticket.TicketCommentRepository;
import bg.sofia.uni.fmi.issuetracker.repository.ticket.TicketRepository;
import bg.sofia.uni.fmi.issuetracker.service.mapper.TicketCommentMapper;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static bg.sofia.uni.fmi.issuetracker.TestData.TEST_TICKET;
import static bg.sofia.uni.fmi.issuetracker.TestData.TEST_TICKET_COMMENT;
import static bg.sofia.uni.fmi.issuetracker.TestData.TEST_USER;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TicketCommentServiceImplTests {
    @Mock
    private TicketCommentRepository ticketCommentRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TicketCommentMapper mapper;

    @InjectMocks
    private TicketCommentServiceImpl service;

    @Test
    void testGetAllCommentsForTicket_UsesDefaultPaginationWhenInvalidPaginationDataIsPassed() {
        Page<TicketComment> page = new PageImpl<>(List.of(TEST_TICKET_COMMENT));

        when(ticketRepository.findById(TEST_TICKET.getCode())).thenReturn(Optional.of(TEST_TICKET));
        when(ticketCommentRepository.findAllByTicket(eq(TEST_TICKET), any(Pageable.class))).thenReturn(page);

        Page<TicketCommentDetailsDTO> result = service.getAllCommentsForTicket(TEST_TICKET.getCode(), 0, -5);

        assertEquals(1, result.getContent().size());
        assertEquals(TEST_TICKET_COMMENT.getContent(), result.getContent().get(0).content());
        assertEquals(TEST_TICKET_COMMENT.getAuthor().getUsername(), result.getContent().get(0).authorUsername());

        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.captor();
        verify(ticketCommentRepository, times(1)).findAllByTicket(eq(TEST_TICKET), pageableArgumentCaptor.capture());
        Pageable pageable = pageableArgumentCaptor.getValue();
        assertEquals(0, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
    }

    @Test
    void testGetAllCommentsForTicket_ThrowsWhenTicketNotFound() {
        when(ticketRepository.findById(TEST_TICKET.getCode())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getAllCommentsForTicket(TEST_TICKET.getCode(), 1, 10))
                .hasMessage(ExceptionMessages.Ticket.ticketNotFound(TEST_TICKET.getCode()))
                .isExactlyInstanceOf(TicketNotFoundException.class);
    }

    @Test
    void testGetTicketComment_ThrowsWhenNotFound() {
        when(ticketCommentRepository.findById(TEST_TICKET_COMMENT.getUuid())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getTicketComment(TEST_TICKET_COMMENT.getUuid()))
                .hasMessage(ExceptionMessages.TicketComment.ticketCommentNotFound(TEST_TICKET_COMMENT.getUuid()))
                .isExactlyInstanceOf(TicketCommentNotFoundException.class);
    }

    @Test
    void testGetTicketComment_ReturnsCorrectly() {
        when(ticketCommentRepository.findById(TEST_TICKET_COMMENT.getUuid())).thenReturn(Optional.of(TEST_TICKET_COMMENT));

        TicketCommentDetailsDTO expected = new TicketCommentDetailsDTO(TEST_TICKET_COMMENT.getUuid(),
                TEST_TICKET_COMMENT.getContent(), TEST_TICKET_COMMENT.getCreatedAt(), TEST_TICKET.getCode(),
                TEST_USER.getUsername());
        TicketCommentDetailsDTO actual = service.getTicketComment(TEST_TICKET_COMMENT.getUuid());

        assertEquals(expected, actual);
    }

    @Test
    void testAddTicketComment_ThrowsWhenAuthorNotFound() {
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addTicketComment(TEST_USER.getUsername(), "TICKET-1", new CreateTicketCommentDTO("text")))
                .hasMessage(ExceptionMessages.User.userNotFound(TEST_USER.getUsername()))
                .isExactlyInstanceOf(UserNotFoundException.class);
    }

    @Test
    void testAddTicketComment_ThrowsWhenTicketNotFound() {
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));
        when(ticketRepository.findById(TEST_TICKET.getCode())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addTicketComment(TEST_USER.getUsername(), TEST_TICKET.getCode(), new CreateTicketCommentDTO("text")))
                .hasMessage(ExceptionMessages.Ticket.ticketNotFound(TEST_TICKET.getCode()))
                .isExactlyInstanceOf(TicketNotFoundException.class);
    }

    @Test
    void testAddTicketComment_Successfully() {
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.of(TEST_USER));
        when(ticketRepository.findById(TEST_TICKET.getCode())).thenReturn(Optional.of(TEST_TICKET));
        doAnswer(_ -> null).when(ticketCommentRepository).save(any());

        CreateTicketCommentDTO dto = new CreateTicketCommentDTO("testContent");
        service.addTicketComment(TEST_USER.getUsername(), TEST_TICKET.getCode(), dto);

        ArgumentCaptor<TicketComment> commentCaptor = ArgumentCaptor.captor();
        verify(ticketCommentRepository, times(1)).save(commentCaptor.capture());
        TicketComment savedComment = commentCaptor.getValue();

        assertEquals(dto.content(), savedComment.getContent());
        assertEquals(TEST_USER, savedComment.getAuthor());
        assertEquals(TEST_TICKET, savedComment.getTicket());
    }

    @Test
    void testUpdateTicketComment_ThrowsWhenNotFound() {
        when(ticketCommentRepository.findById(TEST_TICKET_COMMENT.getUuid())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateTicketComment(TEST_TICKET_COMMENT.getUuid(), new UpdateTicketCommentDTO("updated"), "author"))
                .hasMessage(ExceptionMessages.TicketComment.ticketCommentNotFound(TEST_TICKET_COMMENT.getUuid()))
                .isExactlyInstanceOf(TicketCommentNotFoundException.class);
    }

    @Test
    void testUpdateTicketComment_ThrowsOnOwnershipMismatch() {
        when(ticketCommentRepository.findById(TEST_TICKET_COMMENT.getUuid())).thenReturn(Optional.of(TEST_TICKET_COMMENT));

        assertThatThrownBy(() -> service.updateTicketComment(TEST_TICKET_COMMENT.getUuid(), new UpdateTicketCommentDTO("updated"), "other-user"))
                .hasMessage(ExceptionMessages.TicketComment.allowedToModifyOnlyOwnComments())
                .isExactlyInstanceOf(OwnershipMismatchException.class);
    }

    @Test
    void testUpdateTicketComment_Successfully() {
        when(ticketCommentRepository.findById(TEST_TICKET_COMMENT.getUuid())).thenReturn(Optional.of(TEST_TICKET_COMMENT));

        UpdateTicketCommentDTO dto = new UpdateTicketCommentDTO("updated");
        service.updateTicketComment(TEST_TICKET_COMMENT.getUuid(), dto, TEST_TICKET_COMMENT.getAuthor().getUsername());

        verify(mapper, times(1)).patchTicketCommentFromDTO(dto, TEST_TICKET_COMMENT);
        verify(ticketCommentRepository, times(1)).save(TEST_TICKET_COMMENT);
    }

    @Test
    void testDeleteTicketComment_ThrowsWhenNotFound() {
        when(ticketCommentRepository.findById(TEST_TICKET_COMMENT.getUuid())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteTicketComment(TEST_TICKET_COMMENT.getUuid(), "author"))
                .hasMessage(ExceptionMessages.TicketComment.ticketCommentNotFound(TEST_TICKET_COMMENT.getUuid()))
                .isExactlyInstanceOf(TicketCommentNotFoundException.class);
    }

    @Test
    void testDeleteTicketComment_ThrowsOnOwnershipMismatch() {
        when(ticketCommentRepository.findById(TEST_TICKET_COMMENT.getUuid())).thenReturn(Optional.of(TEST_TICKET_COMMENT));

        assertThatThrownBy(() -> service.deleteTicketComment(TEST_TICKET_COMMENT.getUuid(), "other-user"))
                .hasMessage(ExceptionMessages.TicketComment.allowedToDeleteOnlyOwnComments())
                .isExactlyInstanceOf(OwnershipMismatchException.class);
    }

    @Test
    void testDeleteTicketComment_Successfully() {
        when(ticketCommentRepository.findById(TEST_TICKET_COMMENT.getUuid())).thenReturn(Optional.of(TEST_TICKET_COMMENT));

        service.deleteTicketComment(TEST_TICKET_COMMENT.getUuid(), TEST_TICKET_COMMENT.getAuthor().getUsername());

        verify(ticketCommentRepository, times(1)).delete(TEST_TICKET_COMMENT);
    }
}
