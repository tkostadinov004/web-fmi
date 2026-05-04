package bg.sofia.uni.fmi.issuetracker.controller;

import bg.sofia.uni.fmi.issuetracker.controller.ticket.TicketController;
import bg.sofia.uni.fmi.issuetracker.dto.ticket.TicketRequest;
import bg.sofia.uni.fmi.issuetracker.dto.ticket.TicketResponse;
import bg.sofia.uni.fmi.issuetracker.model.project.Project;
import bg.sofia.uni.fmi.issuetracker.model.sprint.Sprint;
import bg.sofia.uni.fmi.issuetracker.model.ticket.Ticket;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketPriority;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.PriorityOrdered;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(TicketController.class)
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc(addFilters = false)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private bg.sofia.uni.fmi.issuetracker.service.contract.ticket.TicketService ticketService;

    @MockitoBean
    private PriorityOrdered priorityOrdered;

    // For test purposes

    @MockitoBean
    private Sprint sprint;

    @MockitoBean
    private Project project;

    @MockitoBean
    private User user;

    @Test
    void getAllTicketsByProject_returnsTickets() throws Exception {
        TicketResponse response = sampleTicketResponse();

        when(ticketService.getAllTicketsByProjectUuid("project-1"))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/projects/{projectId}/tickets", "project-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uuid").value("ticket-1"))
                .andExpect(jsonPath("$[0].title").value("Fix login"));

        verify(ticketService).getAllTicketsByProjectUuid("project-1");
    }

    @Test
    void getTicketsWithFilters_returnsTickets() throws Exception {
        TicketResponse response = sampleTicketResponse();

        when(ticketService.getAllTicketsByProjectUuidStatusPriorityAndAssigneeUsername(
                eq("project-1"), eq(TicketStatus.TO_DO), eq(TicketPriority.HIGH), eq("john")))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/projects/{projectId}/tickets", "project-1")
                        .param("ticketStatus", "TODO")
                        .param("ticketPriority", "HIGH")
                        .param("assigneeUsername", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uuid").value("ticket-1"));

        verify(ticketService).getAllTicketsByProjectUuidStatusPriorityAndAssigneeUsername(
                "project-1", TicketStatus.TO_DO, TicketPriority.HIGH, "john");
    }

    @Test
    void getTicket_returnsTicket() throws Exception {
        TicketResponse response = sampleTicketResponse();

        when(ticketService.getTicketByProjectUuidAndTicketUuid("project-1", "ticket-1"))
                .thenReturn(Optional.of(response));

        mockMvc.perform(get("/projects/{projectId}/tickets/{ticketId}", "project-1", "ticket-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value("ticket-1"))
                .andExpect(jsonPath("$.title").value("Fix login"));

        verify(ticketService).getTicketByProjectUuidAndTicketUuid("project-1", "ticket-1");
    }

    @Test
    void addTicket_createsTicket() throws Exception {
        TicketRequest request = sampleTicketRequest();
        TicketResponse response = sampleTicketResponse();

        when(ticketService.addTicketByProjectUuid(eq("project-1"), any(TicketRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/projects/{projectId}/tickets", "project-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "uuid": "ticket-1",
                                  "title": "Fix login",
                                  "description": "Login button broken",
                                  "ticketStatus": "TODO",
                                  "ticketPriority": "HIGH",
                                  "sprint": { "uuid": "sprint-1" },
                                  "dueDate": "2026-05-10T12:00:00",
                                  "project": { "uuid": "project-1" },
                                  "assignee": { "username": "john" },
                                  "dependentTicketUuids": [{ "uuid": "dep-1" }]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uuid").value("ticket-1"))
                .andExpect(jsonPath("$.title").value("Fix login"));

        verify(ticketService).addTicketByProjectUuid(eq("project-1"), any(TicketRequest.class));
    }

    @Test
    void updateTicket_updatesTicket() throws Exception {
        TicketResponse response = sampleTicketResponse();

        when(ticketService.updateTicketByProjectUuid(eq("project-1"), eq("ticket-1"), any(TicketRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/projects/{projectId}/tickets/{ticketId}", "project-1", "ticket-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "uuid": "ticket-1",
                                  "title": "Fix login",
                                  "description": "Updated description",
                                  "ticketStatus": "UNDER_DEVELOPMENT",
                                  "ticketPriority": "HIGH",
                                  "sprint": { "uuid": "sprint-1" },
                                  "dueDate": "2026-05-11T12:00:00",
                                  "project": { "uuid": "project-1" },
                                  "assignee": { "username": "john" },
                                  "dependentTicketUuids": [{ "uuid": "dep-1" }]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value("ticket-1"));

        verify(ticketService).updateTicketByProjectUuid(eq("project-1"), eq("ticket-1"), any(TicketRequest.class));
    }

    @Test
    void deleteTicket_deletesTicket() throws Exception {
        mockMvc.perform(delete("/projects/{projectId}/tickets/{ticketId}", "project-1", "ticket-1"))
                .andExpect(status().isNoContent());

        verify(ticketService).deleteTicketByProjectUuid("project-1", "ticket-1");
    }

    private TicketResponse sampleTicketResponse() {
        Ticket ticket = new Ticket("Fix login", "Login button broken", sprint, TicketPriority.HIGH,
                TicketStatus.TO_DO, LocalDateTime.now(), project, user, List.of());

        return TicketResponse.from(ticket);
    }

    private TicketRequest sampleTicketRequest() {
        TicketRequest request = new TicketRequest();
        request.setUuid("ticket-1");
        request.setTitle("Fix login");
        request.setDescription("Login button broken");
        request.setTicketStatus(TicketStatus.TO_DO);
        request.setTicketPriority(TicketPriority.HIGH);
        request.setDueDate(java.time.LocalDateTime.now().plusDays(3));
        return request;
    }
}