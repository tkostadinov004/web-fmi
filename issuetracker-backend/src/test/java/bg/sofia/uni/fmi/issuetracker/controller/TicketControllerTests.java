package bg.sofia.uni.fmi.issuetracker.controller;

import bg.sofia.uni.fmi.issuetracker.dto.input.ticket.CreateTicketCommentDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.ticket.CreateTicketDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.ticket.UpdateTicketDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.ticket.TicketCommentDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.ticket.TicketDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketAlreadyExistsException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketNotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketNotInProjectException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.UnassignedTicketException;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketPriority;
import bg.sofia.uni.fmi.issuetracker.service.contract.TicketCommentService;
import bg.sofia.uni.fmi.issuetracker.service.contract.TicketService;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {TicketController.class})
public class TicketControllerTests extends BaseControllerTests {
    private static final String PROJECT_ID = "project-uuid";

    @MockitoBean
    private TicketService ticketService;

    @MockitoBean
    private TicketCommentService ticketCommentService;

    @Test
    public void testGetTicketByCode_ReturnsOk() throws Exception {
        TicketDetailsDTO dto = new TicketDetailsDTO("TICKET-1", "Title", "Description", "In progress",
                TicketPriority.MEDIUM, LocalDateTime.now(), LocalDateTime.now(), null, PROJECT_ID, null, List.of());
        when(ticketService.getTicketByCode(PROJECT_ID, "TICKET-1")).thenReturn(dto);

        mockMvc.perform(get("/projects/" + PROJECT_ID + "/tickets/TICKET-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("TICKET-1"))
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    public void testGetTicketByCode_ReturnsNotFoundWhenMissing() throws Exception {
        when(ticketService.getTicketByCode(PROJECT_ID, "MISSING")).thenThrow(new TicketNotFoundException(ExceptionMessages.Ticket.ticketNotFound("MISSING", PROJECT_ID)));

        mockMvc.perform(get("/projects/" + PROJECT_ID + "/tickets/MISSING"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.Ticket.ticketNotFound("MISSING", PROJECT_ID)));
    }

    @Test
    public void testCreateTicket_ReturnsOkWhenSuccessful() throws Exception {
        CreateTicketDTO dto = new CreateTicketDTO("CODE-1", "Title", "Details", "In progress",
                TicketPriority.HIGH, LocalDateTime.now().plusDays(2), "assignee");

        mockMvc.perform(post("/projects/" + PROJECT_ID + "/tickets")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateTicket_ReturnsBadRequestOnInvalidData() throws Exception {
        CreateTicketDTO invalid = new CreateTicketDTO("", "", null, null, null, LocalDateTime.now().minusDays(1), null);

        mockMvc.perform(post("/projects/" + PROJECT_ID + "/tickets")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.code").exists())
                .andExpect(jsonPath("$.errors.title").exists())
                .andExpect(jsonPath("$.errors.ticketPriority").exists());
    }

    @Test
    public void testCreateTicket_ReturnsConflictWhenAlreadyExists() throws Exception {
        CreateTicketDTO dto = new CreateTicketDTO("CODE-1", "Title", "Details", "In progress",
                TicketPriority.HIGH, LocalDateTime.now().plusDays(2), null);
        when(ticketService.createTicket(PROJECT_ID, dto)).thenThrow(new TicketAlreadyExistsException(ExceptionMessages.Ticket.ticketAlreadyExists(dto.code(), PROJECT_ID)));

        mockMvc.perform(post("/projects/" + PROJECT_ID + "/tickets")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.Ticket.ticketAlreadyExists(dto.code(), PROJECT_ID)));
    }

    @Test
    public void testAddDependentTicket_ReturnsOkWhenSuccessful() throws Exception {
        CreateTicketDTO dto = new CreateTicketDTO("DEPENDENT-1", "Title", "Details", "In progress",
                TicketPriority.LOW, LocalDateTime.now().plusDays(2), null);

        mockMvc.perform(post("/projects/" + PROJECT_ID + "/tickets/PARENT-1/dependents")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    public void testAddDependentTicket_ReturnsConflictWhenDependentTicketNotInProject() throws Exception {
        CreateTicketDTO dto = new CreateTicketDTO("DEPENDENT-1", "Title", "Details", "In progress",
                TicketPriority.LOW, LocalDateTime.now().plusDays(2), null);
        doThrow(new TicketNotInProjectException(ExceptionMessages.Ticket.ticketProjectMismatch(dto.code(), "PARENT-1")))
                .when(ticketService).addDependentTicketToTicket(PROJECT_ID, "PARENT-1", dto);

        mockMvc.perform(post("/projects/" + PROJECT_ID + "/tickets/PARENT-1/dependents")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.Ticket.ticketProjectMismatch(dto.code(), "PARENT-1")));
    }

    @Test
    public void testChangeAssignee_ReturnsNoContentWhenSuccessful() throws Exception {
        mockMvc.perform(patch("/projects/" + PROJECT_ID + "/tickets/CODE-1/assignee").param("assigneeUsername", "newuser"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testChangeAssignee_ReturnsNotFoundWhenTicketMissing() throws Exception {
        doThrow(new TicketNotFoundException(ExceptionMessages.Ticket.ticketNotFound("MISSING", PROJECT_ID)))
                .when(ticketService).changeTicketAssignee(PROJECT_ID, "MISSING", "newuser");

        mockMvc.perform(patch("/projects/" + PROJECT_ID + "/tickets/MISSING/assignee").param("assigneeUsername", "newuser"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.Ticket.ticketNotFound("MISSING", PROJECT_ID)));
    }

    @Test
    public void testRemoveAssignee_ReturnsNoContentWhenSuccessful() throws Exception {
        mockMvc.perform(delete("/projects/" + PROJECT_ID + "/tickets/CODE-1/assignee"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testRemoveAssignee_ReturnsNotFoundWhenTicketMissing() throws Exception {
        doThrow(new TicketNotFoundException(ExceptionMessages.Ticket.ticketNotFound("MISSING", PROJECT_ID)))
                .when(ticketService).removeTicketAssignee(PROJECT_ID, "MISSING");

        mockMvc.perform(delete("/projects/" + PROJECT_ID + "/tickets/MISSING/assignee"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.Ticket.ticketNotFound("MISSING", PROJECT_ID)));
    }

    @Test
    public void testRemoveAssignee_ReturnsNotFoundWhenTicketIsUnassigned() throws Exception {
        doThrow(new UnassignedTicketException(ExceptionMessages.Ticket.unassignedTicket("MISSING")))
                .when(ticketService).removeTicketAssignee(PROJECT_ID, "MISSING");

        mockMvc.perform(delete("/projects/" + PROJECT_ID + "/tickets/MISSING/assignee"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.Ticket.unassignedTicket("MISSING")));
    }

    @Test
    public void testUpdateTicket_ReturnsNoContentWhenSuccessful() throws Exception {
        UpdateTicketDTO dto = new UpdateTicketDTO("Updated Title", null, "In progress", TicketPriority.HIGHEST, LocalDateTime.now().plusDays(5));

        mockMvc.perform(patch("/projects/" + PROJECT_ID + "/tickets/CODE-1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testUpdateTicket_ReturnsBadRequestOnInvalidData() throws Exception {
        UpdateTicketDTO invalid = new UpdateTicketDTO("", null, null, null, LocalDateTime.now().minusDays(1));

        mockMvc.perform(patch("/projects/" + PROJECT_ID + "/tickets/CODE-1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.dueDate").exists());
    }

    @Test
    public void testDeleteTicket_ReturnsNoContentWhenSuccessful() throws Exception {
        mockMvc.perform(delete("/projects/" + PROJECT_ID + "/tickets/CODE-1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteTicket_ReturnsNotFoundWhenMissing() throws Exception {
        doThrow(new TicketNotFoundException(ExceptionMessages.Ticket.ticketNotFound("MISSING", PROJECT_ID)))
                .when(ticketService).deleteTicket(PROJECT_ID, "MISSING");

        mockMvc.perform(delete("/projects/" + PROJECT_ID + "/tickets/MISSING"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.Ticket.ticketNotFound("MISSING", PROJECT_ID)));
    }

    @Test
    public void testGetAllCommentsForTicket_ReturnsOkAndLinkHeader() throws Exception {
        TicketCommentDetailsDTO comment = new TicketCommentDetailsDTO("UUID-1", "Hello", LocalDateTime.now(), "TICKET-1", "author");
        when(ticketCommentService.getAllCommentsForTicket(PROJECT_ID, "TICKET-1", 1, 10))
                .thenReturn(new PageImpl<>(List.of(comment)));

        mockMvc.perform(get("/projects/" + PROJECT_ID + "/tickets/TICKET-1/comments"))
                .andExpect(status().isOk())
                .andExpect(header().string("Link", containsString("rel=")))
                .andExpect(jsonPath("$[0].uuid").value("UUID-1"));
    }

    @Test
    public void testAddCommentToTicket_ReturnsNoContentWhenSuccessful() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("author", null));
        CreateTicketCommentDTO dto = new CreateTicketCommentDTO("New comment");

        mockMvc.perform(post("/projects/" + PROJECT_ID + "/tickets/TICKET-1/comments")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testAddCommentToTicket_ReturnsBadRequestWhenInvalidData() throws Exception {
        CreateTicketCommentDTO invalid = new CreateTicketCommentDTO("");

        mockMvc.perform(post("/projects/" + PROJECT_ID + "/tickets/TICKET-1/comments")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.content").exists());
    }

    @Test
    public void testUnauthorizedRequest_ReturnsUnauthorized() throws Exception {
        super.unauthorizedRequest(get("/projects/" + PROJECT_ID + "/tickets/TICKET-1"));
    }
}
