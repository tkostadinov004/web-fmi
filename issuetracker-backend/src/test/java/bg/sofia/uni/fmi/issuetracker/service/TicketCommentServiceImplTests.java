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
import bg.sofia.uni.fmi.issuetracker.service.mapper.TicketCommentMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    void testGetAllCommentsForTicket_UsesDefaultPaginationWhenInvalidNumbers() {
        Ticket ticket = new Ticket();
        ticket.setCode("TICKET-1");

        TicketComment comment = new TicketComment(ticket, new User(), "Hello world", LocalDateTime.now());
        Page<TicketComment> page = new PageImpl<>(List.of(comment));

        when(ticketRepository.findById(ticket.getCode())).thenReturn(Optional.of(ticket));
        when(ticketCommentRepository.findAllByTicket(eq(ticket), any(Pageable.class))).thenReturn(page);

        Page<TicketCommentDetailsDTO> result = service.getAllCommentsForTicket(ticket.getCode(), 0, -5);

        assertEquals(1, result.getContent().size());
        assertEquals(comment.getContent(), result.getContent().get(0).content());
        assertEquals(comment.getAuthor().getUsername(), result.getContent().get(0).authorUsername());

        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.captor();
        verify(ticketCommentRepository, times(1)).findAllByTicket(eq(ticket), pageableArgumentCaptor.capture());
        Pageable pageable = pageableArgumentCaptor.getValue();
        assertEquals(0, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
    }

    @Test
    void testGetAllCommentsForTicket_ThrowsWhenTicketNotFound() {
        String ticketCode = "missing-ticket";
        when(ticketRepository.findById(ticketCode)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getAllCommentsForTicket(ticketCode, 1, 10))
                .isExactlyInstanceOf(TicketNotFoundException.class);
    }

    @Test
    void testGetTicketComment_ThrowsWhenNotFound() {
        String commentUuid = "missing-comment";
        when(ticketCommentRepository.findById(commentUuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getTicketComment(commentUuid))
                .isExactlyInstanceOf(TicketCommentNotFoundException.class);
    }

    @Test
    void testGetTicketComment_ReturnsDto() throws Exception {
        Ticket ticket = new Ticket();
        ticket.setCode("TICKET-1");
        User author = User.UserBuilder.newBuilder().username("author").password("pass").build();
        TicketComment comment = new TicketComment(ticket, author, "My comment", LocalDateTime.now());
        setPrivateField(comment, "uuid", "comment-uuid");

        when(ticketCommentRepository.findById(comment.getUuid())).thenReturn(Optional.of(comment));

        TicketCommentDetailsDTO dto = service.getTicketComment(comment.getUuid());

        assertEquals(comment.getUuid(), dto.uuid());
        assertEquals(comment.getContent(), dto.content());
        assertEquals(author.getUsername(), dto.authorUsername());
    }

    @Test
    void testAddTicketComment_ThrowsWhenAuthorNotFound() {
        String authorName = "not-found";
        when(userRepository.findById(authorName)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addTicketComment(authorName, "TICKET-1", new CreateTicketCommentDTO("text")))
                .isExactlyInstanceOf(UserNotFoundException.class);
    }

    @Test
    void testAddTicketComment_ThrowsWhenTicketNotFound() {
        User author = User.UserBuilder.newBuilder().username("author").password("pass").build();
        when(userRepository.findById(author.getUsername())).thenReturn(Optional.of(author));
        when(ticketRepository.findById("missing-ticket")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addTicketComment(author.getUsername(), "missing-ticket", new CreateTicketCommentDTO("text")))
                .isExactlyInstanceOf(TicketNotFoundException.class);
    }

    @Test
    void testAddTicketComment_Successfully() {
        User author = User.UserBuilder.newBuilder().username("author").password("pass").build();
        Ticket ticket = new Ticket();
        ticket.setCode("TICKET-1");
        CreateTicketCommentDTO dto = new CreateTicketCommentDTO("content");

        when(userRepository.findById(author.getUsername())).thenReturn(Optional.of(author));
        when(ticketRepository.findById(ticket.getCode())).thenReturn(Optional.of(ticket));
        doAnswer(invocation -> invocation.getArgument(0)).when(ticketCommentRepository).save(any(TicketComment.class));

        service.addTicketComment(author.getUsername(), ticket.getCode(), dto);

        ArgumentCaptor<TicketComment> commentCaptor = ArgumentCaptor.captor();
        verify(ticketCommentRepository, times(1)).save(commentCaptor.capture());
        TicketComment savedComment = commentCaptor.getValue();

        assertEquals(dto.content(), savedComment.getContent());
        assertEquals(author, savedComment.getAuthor());
        assertEquals(ticket, savedComment.getTicket());
    }

    @Test
    void testUpdateTicketComment_ThrowsWhenNotFound() {
        String uuid = "missing-comment";
        when(ticketCommentRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateTicketComment(uuid, new UpdateTicketCommentDTO("updated"), "author"))
                .isExactlyInstanceOf(TicketCommentNotFoundException.class);
    }

    @Test
    void testUpdateTicketComment_ThrowsOnOwnershipMismatch() {
        Ticket ticket = new Ticket();
        ticket.setCode("TICKET-1");
        User author = User.UserBuilder.newBuilder().username("author").password("pass").build();
        TicketComment comment = new TicketComment(ticket, author, "old", LocalDateTime.now());
        setPrivateField(comment, "uuid", "comment-uuid");

        when(ticketCommentRepository.findById(comment.getUuid())).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> service.updateTicketComment(comment.getUuid(), new UpdateTicketCommentDTO("updated"), "other-user"))
                .isExactlyInstanceOf(OwnershipMismatchException.class);
    }

    @Test
    void testUpdateTicketComment_Successfully() {
        Ticket ticket = new Ticket();
        ticket.setCode("TICKET-1");
        User author = User.UserBuilder.newBuilder().username("author").password("pass").build();
        TicketComment comment = new TicketComment(ticket, author, "old", LocalDateTime.now());
        setPrivateField(comment, "uuid", "comment-uuid");

        when(ticketCommentRepository.findById(comment.getUuid())).thenReturn(Optional.of(comment));

        service.updateTicketComment(comment.getUuid(), new UpdateTicketCommentDTO("updated"), author.getUsername());

        verify(mapper, times(1)).patchTicketCommentFromDTO(new UpdateTicketCommentDTO("updated"), comment);
        verify(ticketCommentRepository, times(1)).save(comment);
    }

    @Test
    void testDeleteTicketComment_ThrowsWhenNotFound() {
        String uuid = "missing-comment";
        when(ticketCommentRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteTicketComment(uuid, "author"))
                .isExactlyInstanceOf(TicketCommentNotFoundException.class);
    }

    @Test
    void testDeleteTicketComment_ThrowsOnOwnershipMismatch() {
        Ticket ticket = new Ticket();
        ticket.setCode("TICKET-1");
        User author = User.UserBuilder.newBuilder().username("author").password("pass").build();
        TicketComment comment = new TicketComment(ticket, author, "old", LocalDateTime.now());
        setPrivateField(comment, "uuid", "comment-uuid");

        when(ticketCommentRepository.findById(comment.getUuid())).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> service.deleteTicketComment(comment.getUuid(), "other-user"))
                .isExactlyInstanceOf(OwnershipMismatchException.class);
    }

    @Test
    void testDeleteTicketComment_Successfully() {
        Ticket ticket = new Ticket();
        ticket.setCode("TICKET-1");
        User author = User.UserBuilder.newBuilder().username("author").password("pass").build();
        TicketComment comment = new TicketComment(ticket, author, "old", LocalDateTime.now());
        setPrivateField(comment, "uuid", "comment-uuid");

        when(ticketCommentRepository.findById(comment.getUuid())).thenReturn(Optional.of(comment));

        service.deleteTicketComment(comment.getUuid(), author.getUsername());

        verify(ticketCommentRepository, times(1)).delete(comment);
    }

    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception _) {

        }
    }
}
