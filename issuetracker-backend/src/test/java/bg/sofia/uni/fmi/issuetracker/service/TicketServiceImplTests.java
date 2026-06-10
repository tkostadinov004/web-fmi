package bg.sofia.uni.fmi.issuetracker.service;

import bg.sofia.uni.fmi.issuetracker.dto.input.ticket.CreateTicketDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.ticket.UpdateTicketDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.ticket.DependentTicketDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.ticket.TicketDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.exception.InvalidWorkflowTransitionException;
import bg.sofia.uni.fmi.issuetracker.exception.project.ProjectNotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.project.UserNotPartOfProjectException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketNotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.UnassignedTicketException;
import bg.sofia.uni.fmi.issuetracker.exception.user.UserNotFoundException;
import bg.sofia.uni.fmi.issuetracker.model.ticket.Ticket;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketPriority;
import bg.sofia.uni.fmi.issuetracker.repository.ProjectRepository;
import bg.sofia.uni.fmi.issuetracker.repository.TicketRepository;
import bg.sofia.uni.fmi.issuetracker.repository.UserRepository;
import bg.sofia.uni.fmi.issuetracker.repository.neo.WorkflowRepository;
import bg.sofia.uni.fmi.issuetracker.service.mapper.TicketMapper;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TicketServiceImplTests {
    private static final List<String> POSSIBLE_STATUSES = List.of("Blocked", "In progress", "In review", "Done");
    private static final String PROJECT_UUID = TEST_PROJECT.getUuid();
    private static final CreateTicketDTO CREATE_TICKET_DTO = new CreateTicketDTO("Ticket-1", "testTitle", "testDescription",
            TicketPriority.HIGH, LocalDateTime.now().plusDays(2), TEST_USER.getUsername());

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkflowRepository workflowRepository;

    @Mock
    private TicketMapper ticketMapper;

    @InjectMocks
    private TicketServiceImpl service;

    @Test
    void testGetTicketByCode_ThrowsWhenTicketNotFound() {
        when(projectRepository.findById(PROJECT_UUID)).thenReturn(Optional.of(TEST_PROJECT));
        when(ticketRepository.findById_CodeAndProject(TEST_TICKET.getCode(), TEST_PROJECT)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getTicketByCode(PROJECT_UUID, TEST_TICKET.getCode()))
                .hasMessage(ExceptionMessages.Ticket.ticketNotFound(TEST_TICKET.getCode(), PROJECT_UUID))
                .isExactlyInstanceOf(TicketNotFoundException.class);
    }

    @Test
    void testGetTicketByCode_ReturnsTicketDetails() {
        when(projectRepository.findById(PROJECT_UUID)).thenReturn(Optional.of(TEST_PROJECT));
        when(ticketRepository.findById_CodeAndProject(TEST_TICKET.getCode(), TEST_PROJECT)).thenReturn(Optional.of(TEST_TICKET));

        TicketDetailsDTO dto = service.getTicketByCode(PROJECT_UUID, TEST_TICKET.getCode());

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
    void testGetDependentTickets_ThrowsWhenTicketNotFound() {
        when(projectRepository.findById(PROJECT_UUID)).thenReturn(Optional.of(TEST_PROJECT));
        when(ticketRepository.findById_CodeAndProject(TEST_TICKET.getCode(), TEST_PROJECT)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getDependentTickets(PROJECT_UUID, TEST_TICKET.getCode()))
                .isExactlyInstanceOf(TicketNotFoundException.class)
                .hasMessage(ExceptionMessages.Ticket.ticketNotFound(TEST_TICKET.getCode(), PROJECT_UUID));
    }

    @Test
    void testGetDependentTickets_ReturnsDependentTicketDTOs() {
        Ticket parentTicket = new Ticket(TEST_TICKET.getCode(), TEST_TICKET.getTitle(), TEST_TICKET.getDescription(), TEST_TICKET.getTicketPriority(),
                TEST_TICKET.getTicketStatus(), TEST_TICKET.getDueDate(), TEST_PROJECT, TEST_USER, new ArrayList<>());
        Ticket dependentTicket = new Ticket("Ticket-2", "dependentTitle", "dependentDescription",
                TicketPriority.MEDIUM, "To do", TEST_TICKET.getDueDate(), TEST_PROJECT, TEST_USER_2, new ArrayList<>());
        parentTicket.addDependentTicket(dependentTicket);

        when(projectRepository.findById(PROJECT_UUID)).thenReturn(Optional.of(TEST_PROJECT));
        when(ticketRepository.findById_CodeAndProject(TEST_TICKET.getCode(), TEST_PROJECT)).thenReturn(Optional.of(parentTicket));

        List<DependentTicketDTO> result = service.getDependentTickets(PROJECT_UUID, TEST_TICKET.getCode());

        assertEquals(1, result.size());
        assertEquals(dependentTicket.getCode(), result.get(0).code());
        assertEquals(dependentTicket.getTitle(), result.get(0).title());
        assertEquals(dependentTicket.getDescription(), result.get(0).description());
        assertEquals(dependentTicket.getTicketStatus(), result.get(0).status());
    }

    @Test
    void testCreateTicket_ThrowsWhenAssigneeNotFound() {
        when(userRepository.findById(CREATE_TICKET_DTO.assigneeUsername())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createTicket(PROJECT_UUID, CREATE_TICKET_DTO))
                .hasMessage(ExceptionMessages.User.userNotFound(CREATE_TICKET_DTO.assigneeUsername()))
                .isExactlyInstanceOf(UserNotFoundException.class);
    }

    @Test
    void testCreateTicket_ThrowsWhenProjectNotFound() {
        when(userRepository.findById(CREATE_TICKET_DTO.assigneeUsername())).thenReturn(Optional.of(TEST_USER));
        when(projectRepository.findById(PROJECT_UUID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createTicket(PROJECT_UUID, CREATE_TICKET_DTO))
                .hasMessage(ExceptionMessages.Project.projectNotFound(PROJECT_UUID))
                .isExactlyInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    void testCreateTicket_ThrowsWhenTicketAlreadyExists() {
        when(userRepository.findById(CREATE_TICKET_DTO.assigneeUsername())).thenReturn(Optional.of(TEST_USER));
        when(projectRepository.findById(PROJECT_UUID)).thenReturn(Optional.of(TEST_PROJECT));
        when(projectRepository.isUserInProject(TEST_USER, TEST_PROJECT)).thenReturn(true);
        when(ticketRepository.existsById_CodeAndProject(CREATE_TICKET_DTO.code(), TEST_PROJECT)).thenReturn(true);

        assertThatThrownBy(() -> service.createTicket(PROJECT_UUID, CREATE_TICKET_DTO))
                .hasMessage(ExceptionMessages.Ticket.ticketAlreadyExists(CREATE_TICKET_DTO.code(), TEST_PROJECT.getUuid()))
                .isExactlyInstanceOf(TicketAlreadyExistsException.class);
    }

    @Test
    void testCreateTicket_ThrowsWhenAssigneeNotInProject() {
        when(userRepository.findById(CREATE_TICKET_DTO.assigneeUsername())).thenReturn(Optional.of(TEST_USER));
        when(projectRepository.findById(PROJECT_UUID)).thenReturn(Optional.of(TEST_PROJECT));
        when(projectRepository.isUserInProject(TEST_USER, TEST_PROJECT)).thenReturn(false);

        assertThatThrownBy(() -> service.createTicket(PROJECT_UUID, CREATE_TICKET_DTO))
                .hasMessage(ExceptionMessages.Project.userNotInProject(CREATE_TICKET_DTO.assigneeUsername(), PROJECT_UUID))
                .isExactlyInstanceOf(UserNotPartOfProjectException.class);
    }

    @Test
    void testCreateTicket_SuccessWithoutAssignee() {
        CreateTicketDTO dtoWithoutAssignee = new CreateTicketDTO("Ticket-1", "testTitle", "testDescription",
                TicketPriority.HIGH, LocalDateTime.now().plusDays(2), null);
        when(projectRepository.findById(PROJECT_UUID)).thenReturn(Optional.of(TEST_PROJECT));
        when(ticketRepository.existsById_CodeAndProject(dtoWithoutAssignee.code(), TEST_PROJECT)).thenReturn(false);
        when(workflowRepository.getInitialStatus(PROJECT_UUID)).thenReturn("To do");
        doAnswer(answer -> answer.getArgument(0)).when(ticketRepository).save(any(Ticket.class));

        Ticket created = service.createTicket(PROJECT_UUID, dtoWithoutAssignee);
        assertEquals(dtoWithoutAssignee.code(), created.getCode());
        assertNull(created.getAssignee());
        assertEquals(TEST_PROJECT, created.getProject());
        assertEquals("To do", created.getTicketStatus());

        verify(ticketRepository, times(1)).save(any());
    }

    @Test
    void testCreateTicket_SuccessWithAssignee() {
        when(ticketRepository.existsById_CodeAndProject(CREATE_TICKET_DTO.code(), TEST_PROJECT)).thenReturn(false);
        when(userRepository.findById(CREATE_TICKET_DTO.assigneeUsername())).thenReturn(Optional.of(TEST_USER));
        when(projectRepository.findById(PROJECT_UUID)).thenReturn(Optional.of(TEST_PROJECT));
        when(projectRepository.isUserInProject(TEST_USER, TEST_PROJECT)).thenReturn(true);
        when(workflowRepository.getInitialStatus(PROJECT_UUID)).thenReturn("To do");
        doAnswer(answer -> answer.getArgument(0)).when(ticketRepository).save(any());

        Ticket created = service.createTicket(PROJECT_UUID, CREATE_TICKET_DTO);

        assertEquals(TEST_USER, created.getAssignee());
        assertEquals(TEST_PROJECT, created.getProject());
    }

    @Test
    void testAddDependentTicketToTicket_ThrowsWhenParentTicketNotFound() {
        when(projectRepository.findById(PROJECT_UUID)).thenReturn(Optional.of(TEST_PROJECT));
        when(ticketRepository.findById_CodeAndProject(TEST_TICKET.getCode(), TEST_PROJECT)).thenReturn(Optional.empty());

        CreateTicketDTO dependentTicketDTO = new CreateTicketDTO("Ticket-2", "testTitle2", "testDescription2",
                TicketPriority.LOW, null, null);
        assertThatThrownBy(() -> service.addDependentTicketToTicket(PROJECT_UUID, TEST_TICKET.getCode(), dependentTicketDTO))
                .hasMessage(ExceptionMessages.Ticket.ticketNotFound(TEST_TICKET.getCode(), PROJECT_UUID))
                .isExactlyInstanceOf(TicketNotFoundException.class);
    }

    @Test
    void testAddDependentTicketToTicket_Successfully() {
        CreateTicketDTO dependentTicketDTO = new CreateTicketDTO("Ticket-2", "testTitle2", "testDescription2",
                TicketPriority.LOW, null, null);

        when(projectRepository.findById(PROJECT_UUID)).thenReturn(Optional.of(TEST_PROJECT));
        when(ticketRepository.findById_CodeAndProject(TEST_TICKET.getCode(), TEST_PROJECT)).thenReturn(Optional.of(TEST_TICKET));
        when(projectRepository.findById(PROJECT_UUID)).thenReturn(Optional.of(TEST_PROJECT));
        when(ticketRepository.existsById_CodeAndProject(dependentTicketDTO.code(), TEST_PROJECT)).thenReturn(false);
        when(workflowRepository.getInitialStatus(PROJECT_UUID)).thenReturn("To do");

        doAnswer(answer -> answer.getArgument(0)).when(ticketRepository).save(any());

        service.addDependentTicketToTicket(PROJECT_UUID, TEST_TICKET.getCode(), dependentTicketDTO);

        assertEquals(1, TEST_TICKET.getDependentTickets().size());
        assertEquals(dependentTicketDTO.code(), TEST_TICKET.getDependentTickets().get(0).getCode());
        verify(ticketRepository, times(1)).save(TEST_TICKET);
    }

    @Test
    void testChangeTicketAssignee_ThrowsWhenTicketNotFound() {
        when(projectRepository.findById(PROJECT_UUID)).thenReturn(Optional.of(TEST_PROJECT));
        when(ticketRepository.findById_CodeAndProject(TEST_TICKET.getCode(), TEST_PROJECT)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.changeTicketAssignee(PROJECT_UUID, TEST_TICKET.getCode(), TEST_USER_2.getUsername()))
                .hasMessage(ExceptionMessages.Ticket.ticketNotFound(TEST_TICKET.getCode(), PROJECT_UUID))
                .isExactlyInstanceOf(TicketNotFoundException.class);
    }

    @Test
    void testChangeTicketAssignee_ThrowsWhenAssigneeNotFound() {
        Ticket ticket = new Ticket("Ticket-1", "testTicket", "testDescription", TicketPriority.HIGH,
                "In progress", LocalDateTime.now().plusDays(1), TEST_PROJECT, TEST_USER, List.of());
        when(projectRepository.findById(PROJECT_UUID)).thenReturn(Optional.of(TEST_PROJECT));
        when(ticketRepository.findById_CodeAndProject(ticket.getCode(), TEST_PROJECT)).thenReturn(Optional.of(ticket));
        when(userRepository.findById(TEST_USER.getUsername())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.changeTicketAssignee(PROJECT_UUID, ticket.getCode(), TEST_USER.getUsername()))
                .hasMessage(ExceptionMessages.User.userNotFound(TEST_USER.getUsername()))
                .isExactlyInstanceOf(UserNotFoundException.class);
    }

    @Test
    void testChangeTicketAssignee_Successfully() {
        Ticket ticket = new Ticket("Ticket-1", "testTicket", "testDescription", TicketPriority.HIGH,
                "In progress", LocalDateTime.now().plusDays(1), TEST_PROJECT, TEST_USER, List.of());
        when(projectRepository.findById(PROJECT_UUID)).thenReturn(Optional.of(TEST_PROJECT));
        when(ticketRepository.findById_CodeAndProject(ticket.getCode(), TEST_PROJECT)).thenReturn(Optional.of(ticket));
        when(userRepository.findById(TEST_USER_2.getUsername())).thenReturn(Optional.of(TEST_USER_2));

        service.changeTicketAssignee(PROJECT_UUID, ticket.getCode(), TEST_USER_2.getUsername());

        assertEquals(TEST_USER_2, ticket.getAssignee());
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    void testRemoveTicketAssignee_ThrowsWhenTicketNotFound() {
        when(projectRepository.findById(PROJECT_UUID)).thenReturn(Optional.of(TEST_PROJECT));
        when(ticketRepository.findById_CodeAndProject(TEST_TICKET.getCode(), TEST_PROJECT)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.removeTicketAssignee(PROJECT_UUID, TEST_TICKET.getCode()))
                .hasMessage(ExceptionMessages.Ticket.ticketNotFound(TEST_TICKET.getCode(), PROJECT_UUID))
                .isExactlyInstanceOf(TicketNotFoundException.class);
    }

    @Test
    void testRemoveTicketAssignee_ThrowsWhenTicketIsCurrentlyUnassigned() {
        Ticket ticket = new Ticket("Ticket-1", "testTicket", "testDescription", TicketPriority.HIGH,
                "In progress", LocalDateTime.now().plusDays(1), TEST_PROJECT, null, List.of());
        when(projectRepository.findById(PROJECT_UUID)).thenReturn(Optional.of(TEST_PROJECT));
        when(ticketRepository.findById_CodeAndProject(ticket.getCode(), TEST_PROJECT)).thenReturn(Optional.of(ticket));

        assertThatThrownBy(() -> service.removeTicketAssignee(PROJECT_UUID, ticket.getCode()))
                .hasMessage(ExceptionMessages.Ticket.unassignedTicket(ticket.getCode()))
                .isExactlyInstanceOf(UnassignedTicketException.class);
    }

    @Test
    void testRemoveTicketAssignee_ClearsAssignee() {
        Ticket ticket = new Ticket("Ticket-1", "testTicket", "testDescription", TicketPriority.HIGH,
                "In progress", LocalDateTime.now().plusDays(1), TEST_PROJECT, TEST_USER, List.of());
        when(projectRepository.findById(PROJECT_UUID)).thenReturn(Optional.of(TEST_PROJECT));
        when(ticketRepository.findById_CodeAndProject(ticket.getCode(), TEST_PROJECT)).thenReturn(Optional.of(ticket));

        service.removeTicketAssignee(PROJECT_UUID, ticket.getCode());

        assertNull(ticket.getAssignee());
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    void testUpdateTicket_ThrowsWhenTicketNotFound() {
        when(projectRepository.findById(PROJECT_UUID)).thenReturn(Optional.of(TEST_PROJECT));
        when(ticketRepository.findById_CodeAndProject(TEST_TICKET.getCode(), TEST_PROJECT)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateTicket(PROJECT_UUID, TEST_TICKET.getCode(), null))
                .hasMessage(ExceptionMessages.Ticket.ticketNotFound(TEST_TICKET.getCode(), PROJECT_UUID))
                .isExactlyInstanceOf(TicketNotFoundException.class);
    }

    @Test
    void testUpdateTicket_ThrowsWhenStatusTransitionIsNotAllowed() {
        when(projectRepository.findById(PROJECT_UUID)).thenReturn(Optional.of(TEST_PROJECT));
        when(ticketRepository.findById_CodeAndProject(TEST_TICKET.getCode(), TEST_PROJECT)).thenReturn(Optional.of(TEST_TICKET));
        when(workflowRepository.isTransitionPossible(PROJECT_UUID, TEST_TICKET.getTicketStatus(), "Done")).thenReturn(false);

        UpdateTicketDTO dto = new UpdateTicketDTO(null, null, "Done", null, null);
        assertThatThrownBy(() -> service.updateTicket(PROJECT_UUID, TEST_TICKET.getCode(), dto))
                .hasMessage(ExceptionMessages.Workflow.invalidStatus(TEST_TICKET.getTicketStatus().toString(), dto.ticketStatus().toString()))
                .isExactlyInstanceOf(InvalidWorkflowTransitionException.class);
    }

    @Test
    void testUpdateTicket_Successfully() {
        when(projectRepository.findById(PROJECT_UUID)).thenReturn(Optional.of(TEST_PROJECT));
        when(ticketRepository.findById_CodeAndProject(TEST_TICKET.getCode(), TEST_PROJECT)).thenReturn(Optional.of(TEST_TICKET));

        UpdateTicketDTO dto = new UpdateTicketDTO(
                "newTitle", "newDescription", null, null, null
        );
        service.updateTicket(PROJECT_UUID, TEST_TICKET.getCode(), dto);

        verify(ticketMapper, times(1)).patchTicketFromDTO(dto, TEST_TICKET);
        verify(ticketRepository, times(1)).save(TEST_TICKET);
    }

    @Test
    void testDeleteTicket_ThrowsWhenNotFound() {
        when(projectRepository.findById(PROJECT_UUID)).thenReturn(Optional.of(TEST_PROJECT));
        when(ticketRepository.findById_CodeAndProject(TEST_TICKET.getCode(), TEST_PROJECT)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteTicket(PROJECT_UUID, TEST_TICKET.getCode()))
                .hasMessage(ExceptionMessages.Ticket.ticketNotFound(TEST_TICKET.getCode(), PROJECT_UUID))
                .isExactlyInstanceOf(TicketNotFoundException.class);
    }

    @Test
    void testDeleteTicket_Successfully() {
        when(projectRepository.findById(PROJECT_UUID)).thenReturn(Optional.of(TEST_PROJECT));
        when(ticketRepository.findById_CodeAndProject(TEST_TICKET.getCode(), TEST_PROJECT)).thenReturn(Optional.of(TEST_TICKET));

        service.deleteTicket(PROJECT_UUID, TEST_TICKET.getCode());

        verify(ticketRepository, times(1)).deleteAll(TEST_TICKET.getDependentTickets());
        verify(ticketRepository, times(1)).delete(TEST_TICKET);
    }
}