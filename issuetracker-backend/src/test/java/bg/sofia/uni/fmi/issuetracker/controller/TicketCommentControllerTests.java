package bg.sofia.uni.fmi.issuetracker.controller;

import bg.sofia.uni.fmi.issuetracker.dto.input.ticket.UpdateTicketCommentDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.ticket.TicketCommentDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.exception.OwnershipMismatchException;
import bg.sofia.uni.fmi.issuetracker.exception.ticket.TicketCommentNotFoundException;
import bg.sofia.uni.fmi.issuetracker.service.contract.ticket.TicketCommentService;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {TicketCommentController.class})
public class TicketCommentControllerTests extends BaseControllerTests {
    @MockitoBean
    private TicketCommentService ticketCommentService;

    @Test
    public void testGetComment_ReturnsOk() throws Exception {
        TicketCommentDetailsDTO dto = new TicketCommentDetailsDTO("UUID-1", "Comment", LocalDateTime.now(), "TICKET-1", "author");
        when(ticketCommentService.getTicketComment("UUID-1")).thenReturn(dto);

        mockMvc.perform(get("/comments/UUID-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value("UUID-1"))
                .andExpect(jsonPath("$.content").value("Comment"));
    }

    @Test
    public void testGetComment_ReturnsNotFoundWhenMissing() throws Exception {
        when(ticketCommentService.getTicketComment("MISSING")).thenThrow(new TicketCommentNotFoundException(ExceptionMessages.TicketComment.ticketCommentNotFound("MISSING")));

        mockMvc.perform(get("/comments/MISSING"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.TicketComment.ticketCommentNotFound("MISSING")));
    }

    @Test
    public void testUpdateTicketComment_ReturnsNoContentWhenSuccessful() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("author", null));
        UpdateTicketCommentDTO dto = new UpdateTicketCommentDTO("Updated comment");

        mockMvc.perform(patch("/comments/UUID-1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testUpdateTicketComment_ReturnsForbiddenWhenNotAuthor() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("author", null));
        UpdateTicketCommentDTO dto = new UpdateTicketCommentDTO("Updated comment");
        doThrow(new OwnershipMismatchException(ExceptionMessages.TicketComment.allowedToModifyOnlyOwnComments())).when(ticketCommentService).updateTicketComment("UUID-1", dto, "author");

        mockMvc.perform(patch("/comments/UUID-1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(dto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.TicketComment.allowedToModifyOnlyOwnComments()));
    }

    @Test
    public void testUpdateTicketComment_ReturnsNotFoundWhenMissing() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("author", null));
        UpdateTicketCommentDTO dto = new UpdateTicketCommentDTO("Updated comment");
        doThrow(new TicketCommentNotFoundException("Comment not found")).when(ticketCommentService).updateTicketComment("MISSING", dto, "author");

        mockMvc.perform(patch("/comments/MISSING")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Comment not found"));
    }

    @Test
    public void testDeleteTicketComment_ReturnsNoContentWhenSuccessful() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("author", null));

        mockMvc.perform(delete("/comments/UUID-1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteTicketComment_ReturnsForbiddenWhenNotAuthor() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("author", null));
        doThrow(new OwnershipMismatchException(ExceptionMessages.TicketComment.allowedToDeleteOnlyOwnComments())).when(ticketCommentService).deleteTicketComment("UUID-1", "author");

        mockMvc.perform(delete("/comments/UUID-1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.TicketComment.allowedToDeleteOnlyOwnComments()));
    }

    @Test
    public void testDeleteTicketComment_ReturnsNotFoundWhenMissing() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("author", null));
        doThrow(new TicketCommentNotFoundException(ExceptionMessages.TicketComment.ticketCommentNotFound("MISSING"))).when(ticketCommentService).deleteTicketComment("MISSING", "author");

        mockMvc.perform(delete("/comments/MISSING"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.TicketComment.ticketCommentNotFound("MISSING")));
    }

    @Test
    public void testUnauthorizedRequest_ReturnsUnauthorized() throws Exception {
        super.unauthorizedRequest(get("/comments/UUID-1"));
    }
}
