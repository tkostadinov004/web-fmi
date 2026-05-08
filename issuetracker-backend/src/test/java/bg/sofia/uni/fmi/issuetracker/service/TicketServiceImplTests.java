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
import bg.sofia.uni.fmi.issuetracker.model.User;
import bg.sofia.uni.fmi.issuetracker.model.project.Project;
import bg.sofia.uni.fmi.issuetracker.model.ticket.Ticket;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketPriority;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketStatus;
import bg.sofia.uni.fmi.issuetracker.repository.ProjectRepository;
import bg.sofia.uni.fmi.issuetracker.repository.UserRepository;
import bg.sofia.uni.fmi.issuetracker.repository.ticket.TicketRepository;
import bg.sofia.uni.fmi.issuetracker.service.mapper.TicketMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TicketServiceImplTests {
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
    void testGetTicketByCode_ThrowsWhenNotFound() {
        String code = "TICKET-1";
        when(ticketRepository.findById(code)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getTicketByCode(code))
                .isExactlyInstanceOf(TicketNotFoundException.class);
    }

    @Test
    void testGetTicketByCode_ReturnsTicketDetails() {
        Project project = new Project("Project A", null, new ArrayList<>());
        setPrivateField(project, "uuid", "proj-1");
        Ticket ticket = new Ticket("TICKET-1", "Title", "Description", TicketPriority.MEDIUM,
                TicketStatus.TO_DO, LocalDateTime.now().plusDays(3), project, null, new ArrayList<>());

        when(ticketRepository.findById(ticket.getCode())).thenReturn(Optional.of(ticket));

        TicketDetailsDTO dto = service.getTicketByCode(ticket.getCode());

        assertEquals(ticket.getCode(), dto.code());
        assertEquals(ticket.getTitle(), dto.title());
        assertEquals(ticket.getProject().getUuid(), dto.projectUuid());
    }

    @Test
    void testGetAllTicketsByProject_ThrowsWhenProjectNotFound() {
        String projectUuid = "missing-project";
        when(projectRepository.findById(projectUuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getAllTicketsByProject(projectUuid))
                .isExactlyInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    void testGetAllTicketsByProject_ReturnsMappedTickets() {
        Project project = new Project("Project A", null, new ArrayList<>());
        setPrivateField(project, "uuid", "proj-1");

        Ticket ticket = new Ticket("TICKET-1", "Title", "Description", TicketPriority.HIGH,
                TicketStatus.TO_DO, LocalDateTime.now().plusDays(5), project, null, new ArrayList<>());
        project.setTickets(List.of(ticket));

        when(projectRepository.findById(project.getUuid())).thenReturn(Optional.of(project));

        List<TicketDetailsDTO> result = service.getAllTicketsByProject(project.getUuid());

        assertEquals(1, result.size());
        assertEquals(ticket.getCode(), result.get(0).code());
    }

    @Test
    void testCreateTicket_ThrowsWhenTicketAlreadyExists() {
        CreateTicketDTO dto = new CreateTicketDTO("TICKET-1", "Title", "Description", TicketStatus.TO_DO,
                TicketPriority.MEDIUM, "proj-1", null, null);
        when(ticketRepository.existsById(dto.code())).thenReturn(true);

        assertThatThrownBy(() -> service.createTicket(dto))
                .isExactlyInstanceOf(TicketAlreadyExistsException.class);
    }

    @Test
    void testCreateTicket_ThrowsWhenAssigneeNotFound() {
        CreateTicketDTO dto = new CreateTicketDTO("TICKET-1", "Title", "Description", TicketStatus.TO_DO,
                TicketPriority.MEDIUM, "proj-1", LocalDateTime.now().plusDays(1), "assignee");
        when(ticketRepository.existsById(dto.code())).thenReturn(false);
        when(userRepository.findById(dto.assigneeUsername())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createTicket(dto))
                .isExactlyInstanceOf(UserNotFoundException.class);
    }

    @Test
    void testCreateTicket_ThrowsWhenProjectNotFound() {
        CreateTicketDTO dto = new CreateTicketDTO("TICKET-1", "Title", "Description", TicketStatus.TO_DO,
                TicketPriority.MEDIUM, "proj-1", null, null);
        when(ticketRepository.existsById(dto.code())).thenReturn(false);
        when(projectRepository.findById(dto.projectUuid())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createTicket(dto))
                .isExactlyInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    void testCreateTicket_ThrowsWhenAssigneeNotInProject() {
        User assignee = User.UserBuilder.newBuilder().username("assignee").password("pass").build();
        Project project = new Project("Project A", null, new ArrayList<>());
        setPrivateField(project, "uuid", "proj-1");
        CreateTicketDTO dto = new CreateTicketDTO("TICKET-1", "Title", "Description", TicketStatus.TO_DO,
                TicketPriority.MEDIUM, project.getUuid(), null, assignee.getUsername());

        when(ticketRepository.existsById(dto.code())).thenReturn(false);
        when(userRepository.findById(assignee.getUsername())).thenReturn(Optional.of(assignee));
        when(projectRepository.findById(dto.projectUuid())).thenReturn(Optional.of(project));
        when(projectRepository.isUserInProject(assignee, project)).thenReturn(false);

        assertThatThrownBy(() -> service.createTicket(dto))
                .isExactlyInstanceOf(UserNotPartOfProjectException.class);
    }

    @Test
    void testCreateTicket_SuccessWithoutAssignee() {
        Project project = new Project("Project A", null, new ArrayList<>());
        setPrivateField(project, "uuid", "proj-1");
        CreateTicketDTO dto = new CreateTicketDTO("TICKET-1", "Title", "Description", TicketStatus.TO_DO,
                TicketPriority.LOW, project.getUuid(), LocalDateTime.now().plusDays(1), null);

        when(ticketRepository.existsById(dto.code())).thenReturn(false);
        when(projectRepository.findById(dto.projectUuid())).thenReturn(Optional.of(project));
        doAnswer(invocation -> invocation.getArgument(0)).when(ticketRepository).save(any(Ticket.class));

        Ticket created = service.createTicket(dto);

        assertEquals(dto.code(), created.getCode());
        assertEquals(dto.assigneeUsername(), created.getAssignee() == null ? null : created.getAssignee().getUsername());
        assertEquals(project, created.getProject());
    }

    @Test
    void testCreateTicket_SuccessWithAssignee() {
        User assignee = User.UserBuilder.newBuilder().username("assignee").password("pass").build();
        Project project = new Project("Project A", null, new ArrayList<>());
        setPrivateField(project, "uuid", "proj-1");
        CreateTicketDTO dto = new CreateTicketDTO("TICKET-1", "Title", "Description", TicketStatus.TO_DO,
                TicketPriority.LOW, project.getUuid(), LocalDateTime.now().plusDays(1), assignee.getUsername());

        when(ticketRepository.existsById(dto.code())).thenReturn(false);
        when(userRepository.findById(assignee.getUsername())).thenReturn(Optional.of(assignee));
        when(projectRepository.findById(dto.projectUuid())).thenReturn(Optional.of(project));
        when(projectRepository.isUserInProject(assignee, project)).thenReturn(true);
        doAnswer(invocation -> invocation.getArgument(0)).when(ticketRepository).save(any(Ticket.class));

        Ticket created = service.createTicket(dto);

        assertEquals(assignee, created.getAssignee());
        assertEquals(project, created.getProject());
    }

    @Test
    void testAddDependentTicketToTicket_ThrowsWhenParentTicketNotFound() {
        String parentCode = "PARENT-1";
        when(ticketRepository.findById(parentCode)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addDependentTicketToTicket(parentCode, new CreateTicketDTO("TICKET-2", "Title", "Description", TicketStatus.TO_DO,
                TicketPriority.LOW, "proj-1", null, null)))
                .isExactlyInstanceOf(TicketNotFoundException.class);
    }

    @Test
    void testAddDependentTicketToTicket_ThrowsWhenTicketProjectMismatch() {
        Project parentProject = new Project("Parent Project", null, new ArrayList<>());
        setPrivateField(parentProject, "uuid", "proj-parent");
        Ticket parentTicket = new Ticket("PARENT-1", "Parent", "Desc", TicketPriority.LOW,
                TicketStatus.TO_DO, LocalDateTime.now().plusDays(2), parentProject, null, new ArrayList<>());

        Project childProject = new Project("Child Project", null, new ArrayList<>());
        setPrivateField(childProject, "uuid", "proj-child");

        CreateTicketDTO dependentTicketDTO = new CreateTicketDTO("CHILD-1", "Child", "Desc", TicketStatus.TO_DO,
                TicketPriority.LOW, childProject.getUuid(), null, null);

        when(ticketRepository.findById(parentTicket.getCode())).thenReturn(Optional.of(parentTicket));
        
        assertThatThrownBy(() -> service.addDependentTicketToTicket(parentTicket.getCode(), dependentTicketDTO))
                .isExactlyInstanceOf(TicketNotInProjectException.class);
    }

    @Test
    void testAddDependentTicketToTicket_Successfully() {
        Project project = new Project("Project A", null, new ArrayList<>());
        setPrivateField(project, "uuid", "proj-1");
        Ticket parentTicket = new Ticket("PARENT-1", "Parent", "Desc", TicketPriority.LOW,
                TicketStatus.TO_DO, LocalDateTime.now().plusDays(2), project, null, new ArrayList<>());
        List<Ticket> dependentTickets = new ArrayList<>();
        parentTicket.setDependentTickets(dependentTickets);

        CreateTicketDTO dependentTicketDTO = new CreateTicketDTO("CHILD-1", "Child", "Desc", TicketStatus.TO_DO,
                TicketPriority.LOW, project.getUuid(), null, null);

        when(ticketRepository.findById(parentTicket.getCode())).thenReturn(Optional.of(parentTicket));
        when(ticketRepository.existsById(dependentTicketDTO.code())).thenReturn(false);
        when(projectRepository.findById(dependentTicketDTO.projectUuid())).thenReturn(Optional.of(project));
        doAnswer(invocation -> invocation.getArgument(0)).when(ticketRepository).save(any(Ticket.class));

        service.addDependentTicketToTicket(parentTicket.getCode(), dependentTicketDTO);

        assertEquals(1, parentTicket.getDependentTickets().size());
        assertEquals(dependentTicketDTO.code(), parentTicket.getDependentTickets().get(0).getCode());
        verify(ticketRepository, times(1)).save(parentTicket);
    }

    @Test
    void testChangeTicketAssignee_ClearsAssignee() {
        Project project = new Project("Project A", null, new ArrayList<>());
        setPrivateField(project, "uuid", "proj-1");
        Ticket ticket = new Ticket("TICKET-1", "Title", "Description", TicketPriority.MEDIUM,
                TicketStatus.TO_DO, LocalDateTime.now().plusDays(2), project,
                User.UserBuilder.newBuilder().username("assignee").password("pass").build(), new ArrayList<>());

        when(ticketRepository.findById(ticket.getCode())).thenReturn(Optional.of(ticket));

        service.changeTicketAssignee(ticket.getCode(), null);

        assertEquals(null, ticket.getAssignee());
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    void testChangeTicketAssignee_ThrowsWhenAssigneeNotFound() {
        Project project = new Project("Project A", null, new ArrayList<>());
        setPrivateField(project, "uuid", "proj-1");
        Ticket ticket = new Ticket("TICKET-1", "Title", "Description", TicketPriority.MEDIUM,
                TicketStatus.TO_DO, LocalDateTime.now().plusDays(2), project, null, new ArrayList<>());

        when(ticketRepository.findById(ticket.getCode())).thenReturn(Optional.of(ticket));
        when(userRepository.findById("missing-assignee")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.changeTicketAssignee(ticket.getCode(), "missing-assignee"))
                .isExactlyInstanceOf(UserNotFoundException.class);
    }

    @Test
    void testChangeTicketAssignee_Successfully() {
        User assignee = User.UserBuilder.newBuilder().username("assignee").password("pass").build();
        Project project = new Project("Project A", null, new ArrayList<>());
        setPrivateField(project, "uuid", "proj-1");
        Ticket ticket = new Ticket("TICKET-1", "Title", "Description", TicketPriority.MEDIUM,
                TicketStatus.TO_DO, LocalDateTime.now().plusDays(2), project, null, new ArrayList<>());

        when(ticketRepository.findById(ticket.getCode())).thenReturn(Optional.of(ticket));
        when(userRepository.findById(assignee.getUsername())).thenReturn(Optional.of(assignee));

        service.changeTicketAssignee(ticket.getCode(), assignee.getUsername());

        assertEquals(assignee, ticket.getAssignee());
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    void testUpdateTicket_ThrowsWhenTicketNotFound() {
        when(ticketRepository.findById("missing-ticket")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateTicket("missing-ticket", new UpdateTicketDTO(null, null, null, null, null)))
                .isExactlyInstanceOf(TicketNotFoundException.class);
    }

    @Test
    void testUpdateTicket_Successfully() {
        Project project = new Project("Project A", null, new ArrayList<>());
        setPrivateField(project, "uuid", "proj-1");
        Ticket ticket = new Ticket("TICKET-1", "Title", "Description", TicketPriority.MEDIUM,
                TicketStatus.TO_DO, LocalDateTime.now().plusDays(2), project, null, new ArrayList<>());
        UpdateTicketDTO dto = new UpdateTicketDTO("New title", "New description", TicketStatus.IN_PROGRESS,
                TicketPriority.HIGH, LocalDateTime.now().plusDays(10));

        when(ticketRepository.findById(ticket.getCode())).thenReturn(Optional.of(ticket));

        service.updateTicket(ticket.getCode(), dto);

        verify(ticketMapper, times(1)).patchTicketFromDTO(dto, ticket);
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    void testDeleteTicket_ThrowsWhenNotFound() {
        when(ticketRepository.findById("missing-ticket")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteTicket("missing-ticket"))
                .isExactlyInstanceOf(TicketNotFoundException.class);
    }

    @Test
    void testDeleteTicket_Successfully() {
        Project project = new Project("Project A", null, new ArrayList<>());
        setPrivateField(project, "uuid", "proj-1");
        Ticket dependent = new Ticket("DEPENDENT-1", "Child", "Desc", TicketPriority.LOW,
                TicketStatus.TO_DO, LocalDateTime.now().plusDays(4), project, null, new ArrayList<>());
        Ticket ticket = new Ticket("TICKET-1", "Title", "Description", TicketPriority.MEDIUM,
                TicketStatus.TO_DO, LocalDateTime.now().plusDays(2), project, null, new ArrayList<>(List.of(dependent)));

        when(ticketRepository.findById(ticket.getCode())).thenReturn(Optional.of(ticket));

        service.deleteTicket(ticket.getCode());

        verify(ticketRepository, times(1)).deleteAll(ticket.getDependentTickets());
        verify(ticketRepository, times(1)).delete(ticket);
    }

    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }
}
