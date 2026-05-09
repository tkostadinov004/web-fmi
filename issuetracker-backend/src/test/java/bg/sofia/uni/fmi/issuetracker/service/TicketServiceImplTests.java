package bg.sofia.uni.fmi.issuetracker.service;

import bg.sofia.uni.fmi.issuetracker.dto.input.ticket.CreateTicketDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.ticket.UpdateTicketDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.ticket.TicketDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectNotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.project.UserNotPartOfProjectException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketNotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketNotInProjectException;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserNotFoundException;
import bg.sofia.uni.fmi.issuetracker.model.ticket.Ticket;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketPriority;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketStatus;
import bg.sofia.uni.fmi.issuetracker.repository.ProjectRepository;
import bg.sofia.uni.fmi.issuetracker.repository.UserRepository;
import bg.sofia.uni.fmi.issuetracker.repository.ticket.TicketRepository;
import bg.sofia.uni.fmi.issuetracker.service.mapper.TicketMapper;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static bg.sofia.uni.fmi.issuetracker.TestData.TEST_PROJECT;
import static bg.sofia.uni.fmi.issuetracker.TestData.TEST_TICKET;
import static bg.sofia.uni.fmi.issuetracker.TestData.TEST_USER;
import static bg.sofia.uni.fmi.issuetracker.TestData.TEST_USER_2;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TicketServiceImplTests {
    private static final CreateTicketDTO CREATE_TICKET_DTO = new CreateTicketDTO("Ticket-1", "testTitle", "testDescription", TicketStatus.TO_DO,
            TicketPriority.HIGH, "proj-1", LocalDateTime.now().plusDays(2), TEST_USER.getUsername());

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TicketMapper ticketMapper;

    @InjectMocks
    private TicketServiceImpl service;

    @Test
    void testGetTicketByCode_ThrowsWhenTicketNotFound() {
        when(ticketRepository.findById(TEST_TICKET.getCode())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getTicketByCode(TEST_TICKET.getCode()))
                .hasMessage(ExceptionMessages.Ticket.ticketNotFound(TEST_TICKET.getCode()))
                .isExactlyInstanceOf(TicketNotFoundException.class);
    }

    @Test
    void testGetTicketByCode_ReturnsTicketDetails() {
        when(ticketRepository.findById(TEST_TICKET.getCode())).thenReturn(Optional.of(TEST_TICKET));

        TicketDetailsDTO dto = service.getTicketByCode(TEST_TICKET.getCode());

        assertEquals(TEST_TICKET.getCode(), dto.code());
        assertEquals(TEST_TICKET.getTitle(), dto.title());
        assertEquals(TEST_TICKET.getProject().getUuid(), dto.projectUuid());
    }

    @Test
    void testGetAllTicketsByProject_ThrowsWhenProjectNotFound() {
        when(projectRepository.findById(TEST_PROJECT.getUuid())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getAllTicketsByProject(TEST_PROJECT.getUuid()))
                .hasMessage(ExceptionMessages.Project.projectNotFound(TEST_PROJECT.getUuid()))
                .isExactlyInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    void testGetAllTicketsByProject_ReturnsMappedTicketsSuccessfully() {
        when(projectRepository.findById(TEST_PROJECT.getUuid())).thenReturn(Optional.of(TEST_PROJECT));

        List<TicketDetailsDTO> result = service.getAllTicketsByProject(TEST_PROJECT.getUuid());

        assertEquals(1, result.size());
        assertEquals(TEST_TICKET.getCode(), result.get(0).code());
    }

    @Test
    void testCreateTicket_ThrowsWhenTicketAlreadyExists() {
        when(ticketRepository.existsById(CREATE_TICKET_DTO.code())).thenReturn(true);

        assertThatThrownBy(() -> service.createTicket(CREATE_TICKET_DTO))
                .hasMessage(ExceptionMessages.Ticket.ticketAlreadyExists(CREATE_TICKET_DTO.code()))
                .isExactlyInstanceOf(TicketAlreadyExistsException.class);
    }

    @Test
    void testCreateTicket_ThrowsWhenAssigneeNotFound() {
        when(ticketRepository.existsById(CREATE_TICKET_DTO.code())).thenReturn(false);
        when(userRepository.findById(CREATE_TICKET_DTO.assigneeUsername())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createTicket(CREATE_TICKET_DTO))
                .hasMessage(ExceptionMessages.User.userNotFound(CREATE_TICKET_DTO.assigneeUsername()))
                .isExactlyInstanceOf(UserNotFoundException.class);
    }

    @Test
    void testCreateTicket_ThrowsWhenProjectNotFound() {
        when(ticketRepository.existsById(CREATE_TICKET_DTO.code())).thenReturn(false);
        when(userRepository.findById(CREATE_TICKET_DTO.assigneeUsername())).thenReturn(Optional.of(TEST_USER));
        when(projectRepository.findById(CREATE_TICKET_DTO.projectUuid())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createTicket(CREATE_TICKET_DTO))
                .hasMessage(ExceptionMessages.Project.projectNotFound(CREATE_TICKET_DTO.projectUuid()))
                .isExactlyInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    void testCreateTicket_ThrowsWhenAssigneeNotInProject() {
        when(ticketRepository.existsById(CREATE_TICKET_DTO.code())).thenReturn(false);
        when(userRepository.findById(CREATE_TICKET_DTO.assigneeUsername())).thenReturn(Optional.of(TEST_USER));
        when(projectRepository.findById(CREATE_TICKET_DTO.projectUuid())).thenReturn(Optional.of(TEST_PROJECT));
        when(projectRepository.isUserInProject(TEST_USER, TEST_PROJECT)).thenReturn(false);

        assertThatThrownBy(() -> service.createTicket(CREATE_TICKET_DTO))
                .hasMessage(ExceptionMessages.Project.userNotInProject(CREATE_TICKET_DTO.assigneeUsername(), CREATE_TICKET_DTO.projectUuid()))
                .isExactlyInstanceOf(UserNotPartOfProjectException.class);
    }

    @Test
    void testCreateTicket_SuccessWithoutAssignee() {
        CreateTicketDTO dtoWithoutAssignee = new CreateTicketDTO("Ticket-1", "testTitle", "testDescription", TicketStatus.TO_DO,
                TicketPriority.HIGH, "proj-1", LocalDateTime.now().plusDays(2), null);
        when(ticketRepository.existsById(dtoWithoutAssignee.code())).thenReturn(false);
        when(projectRepository.findById(dtoWithoutAssignee.projectUuid())).thenReturn(Optional.of(TEST_PROJECT));
        doAnswer(answer -> answer.getArgument(0)).when(ticketRepository).save(any(Ticket.class));

        Ticket created = service.createTicket(dtoWithoutAssignee);
        assertEquals(dtoWithoutAssignee.code(), created.getCode());
        assertNull(created.getAssignee());
        assertEquals(TEST_PROJECT, created.getProject());

        verify(ticketRepository, times(1)).save(any());
    }

    @Test
    void testCreateTicket_SuccessWithAssignee() {
        when(ticketRepository.existsById(CREATE_TICKET_DTO.code())).thenReturn(false);
        when(userRepository.findById(CREATE_TICKET_DTO.assigneeUsername())).thenReturn(Optional.of(TEST_USER));
        when(projectRepository.findById(CREATE_TICKET_DTO.projectUuid())).thenReturn(Optional.of(TEST_PROJECT));
        when(projectRepository.isUserInProject(TEST_USER, TEST_PROJECT)).thenReturn(true);
        doAnswer(answer -> answer.getArgument(0)).when(ticketRepository).save(any());

        Ticket created = service.createTicket(CREATE_TICKET_DTO);

        assertEquals(TEST_USER, created.getAssignee());
        assertEquals(TEST_PROJECT, created.getProject());
    }

    @Test
    void testAddDependentTicketToTicket_ThrowsWhenParentTicketNotFound() {
        when(ticketRepository.findById(TEST_TICKET.getCode())).thenReturn(Optional.empty());

        CreateTicketDTO dependentTicketDTO = new CreateTicketDTO("Ticket-2", "testTitle2", "testDescription2", TicketStatus.TO_DO,
                TicketPriority.LOW, TEST_PROJECT.getUuid(), null, null);
        assertThatThrownBy(() -> service.addDependentTicketToTicket(TEST_TICKET.getCode(), dependentTicketDTO))
                .hasMessage(ExceptionMessages.Ticket.ticketNotFound(TEST_TICKET.getCode()))
                .isExactlyInstanceOf(TicketNotFoundException.class);
    }

    @Test
    void testAddDependentTicketToTicket_ThrowsWhenDependentTicketIsNotInTheSameProjectAsParent() {
        CreateTicketDTO dependentTicketDTO = new CreateTicketDTO("Ticket-2", "testTitle2", "testDescription2", TicketStatus.TO_DO,
                TicketPriority.LOW, "something else", null, null);

        when(ticketRepository.findById(TEST_TICKET.getCode())).thenReturn(Optional.of(TEST_TICKET));

        assertThatThrownBy(() -> service.addDependentTicketToTicket(TEST_TICKET.getCode(), dependentTicketDTO))
                .hasMessage(ExceptionMessages.Ticket.ticketProjectMismatch(dependentTicketDTO.code(), TEST_TICKET.getCode()))
                .isExactlyInstanceOf(TicketNotInProjectException.class);
    }

    @Test
    void testAddDependentTicketToTicket_Successfully() {
        CreateTicketDTO dependentTicketDTO = new CreateTicketDTO("Ticket-2", "testTitle2", "testDescription2", TicketStatus.TO_DO,
                TicketPriority.LOW, TEST_PROJECT.getUuid(), null, null);

        when(ticketRepository.findById(TEST_TICKET.getCode())).thenReturn(Optional.of(TEST_TICKET));
        when(ticketRepository.existsById(dependentTicketDTO.code())).thenReturn(false);
        when(projectRepository.findById(dependentTicketDTO.projectUuid())).thenReturn(Optional.of(TEST_PROJECT));
        doAnswer(answer -> answer.getArgument(0)).when(ticketRepository).save(any());

        service.addDependentTicketToTicket(TEST_TICKET.getCode(), dependentTicketDTO);

        assertEquals(1, TEST_TICKET.getDependentTickets().size());
        assertEquals(dependentTicketDTO.code(), TEST_TICKET.getDependentTickets().get(0).getCode());
        verify(ticketRepository, times(1)).save(TEST_TICKET);
    }

    @Test
    void testChangeTicketAssignee_ThrowsWhenTicketNotFound() {
        when(ticketRepository.findById(TEST_TICKET.getCode())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.changeTicketAssignee(TEST_TICKET.getCode(), TEST_USER_2.getUsername()))
                .hasMessage(ExceptionMessages.Ticket.ticketNotFound(TEST_TICKET.getCode()))
                .isExactlyInstanceOf(TicketNotFoundException.class);
    }

    @Test
    void testChangeTicketAssignee_ClearsAssignee() {
        Ticket ticket = new Ticket("Ticket-1", "testTicket", "testDescription", TicketPriority.HIGH,
                TicketStatus.IN_PROGRESS, LocalDateTime.now().plusDays(1), TEST_PROJECT, TEST_USER, List.of());
        when(ticketRepository.findById(ticket.getCode())).thenReturn(Optional.of(ticket));

        service.changeTicketAssignee(ticket.getCode(), null);

        assertNull(ticket.getAssignee());
        verify(ticketRepository, times(1)).save(ticket);
        verify(userRepository, never()).findById(any());
    }

    @Test
    void testChangeTicketAssignee_ThrowsWhenAssigneeNotFound() {
        Ticket ticket = new Ticket("Ticket-1", "testTicket", "testDescription", TicketPriority.HIGH,
                TicketStatus.IN_PROGRESS, LocalDateTime.now().plusDays(1), TEST_PROJECT, TEST_USER, List.of());
        when(ticketRepository.findById(ticket.getCode())).thenReturn(Optional.of(ticket));
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.changeTicketAssignee(ticket.getCode(), TEST_USER.getUsername()))
                .hasMessage(ExceptionMessages.User.userNotFound(TEST_USER.getUsername()))
                .isExactlyInstanceOf(UserNotFoundException.class);
    }

    @Test
    void testChangeTicketAssignee_Successfully() {
        Ticket ticket = new Ticket("Ticket-1", "testTicket", "testDescription", TicketPriority.HIGH,
                TicketStatus.IN_PROGRESS, LocalDateTime.now().plusDays(1), TEST_PROJECT, TEST_USER, List.of());
        when(ticketRepository.findById(ticket.getCode())).thenReturn(Optional.of(ticket));
        when(userRepository.findById(TEST_USER_2.getUsername())).thenReturn(Optional.of(TEST_USER_2));

        service.changeTicketAssignee(ticket.getCode(), TEST_USER_2.getUsername());

        assertEquals(TEST_USER_2, ticket.getAssignee());
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    void testUpdateTicket_ThrowsWhenTicketNotFound() {
        when(ticketRepository.findById(TEST_TICKET.getCode())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateTicket(TEST_TICKET.getCode(), null))
                .hasMessage(ExceptionMessages.Ticket.ticketNotFound(TEST_TICKET.getCode()))
                .isExactlyInstanceOf(TicketNotFoundException.class);
    }

    @Test
    void testUpdateTicket_Successfully() {
        when(ticketRepository.findById(TEST_TICKET.getCode())).thenReturn(Optional.of(TEST_TICKET));

        UpdateTicketDTO dto = new UpdateTicketDTO(
                "newTitle", "newDescription", null, null, null
        );
        service.updateTicket(TEST_TICKET.getCode(), dto);

        verify(ticketMapper, times(1)).patchTicketFromDTO(dto, TEST_TICKET);
        verify(ticketRepository, times(1)).save(TEST_TICKET);
    }

    @Test
    void testDeleteTicket_ThrowsWhenNotFound() {
        when(ticketRepository.findById(TEST_TICKET.getCode())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteTicket(TEST_TICKET.getCode()))
                .hasMessage(ExceptionMessages.Ticket.ticketNotFound(TEST_TICKET.getCode()))
                .isExactlyInstanceOf(TicketNotFoundException.class);
    }

    @Test
    void testDeleteTicket_Successfully() {
        when(ticketRepository.findById(TEST_TICKET.getCode())).thenReturn(Optional.of(TEST_TICKET));

        service.deleteTicket(TEST_TICKET.getCode());

        verify(ticketRepository, times(1)).deleteAll(TEST_TICKET.getDependentTickets());
        verify(ticketRepository, times(1)).delete(TEST_TICKET);
    }
}
