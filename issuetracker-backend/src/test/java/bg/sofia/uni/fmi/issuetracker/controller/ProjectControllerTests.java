package bg.sofia.uni.fmi.issuetracker.controller;

import bg.sofia.uni.fmi.issuetracker.dto.output.ticket.TicketDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketNotFoundException;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketPriority;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketStatus;
import bg.sofia.uni.fmi.issuetracker.service.contract.ticket.TicketService;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static bg.sofia.uni.fmi.issuetracker.TestData.TEST_PROJECT;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ProjectController.class})
public class ProjectControllerTests extends BaseControllerTests {
    @MockitoBean
    private TicketService ticketService;

    @Test
    public void testGetAllTicketsByProject_ReturnsOk() throws Exception {
        TicketDetailsDTO dto = new TicketDetailsDTO("Ticket-1", "Title", "Description", TicketStatus.IN_PROGRESS,
                TicketPriority.HIGH, LocalDateTime.now(), LocalDateTime.now(), null, "project-uuid", null, List.of());
        when(ticketService.getAllTicketsByProject(dto.projectUuid())).thenReturn(List.of(dto));

        mockMvc.perform(get("/projects/%s/tickets".formatted(dto.projectUuid())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value(dto.code()));
    }

    @Test
    public void testGetAllTicketsByProject_ReturnsNotFoundWhenMissing() throws Exception {
        when(ticketService.getAllTicketsByProject(TEST_PROJECT.getUuid()))
                .thenThrow(new TicketNotFoundException(ExceptionMessages.Project.projectNotFound(TEST_PROJECT.getUuid())));

        mockMvc.perform(get("/projects/%s/tickets".formatted(TEST_PROJECT.getUuid())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.Project.projectNotFound(TEST_PROJECT.getUuid())));
    }

    @Test
    public void testUnauthorizedRequest_ReturnsUnauthorized() throws Exception {
        super.unauthorizedRequest(get("/projects/project-uuid/tickets"));
    }
}
