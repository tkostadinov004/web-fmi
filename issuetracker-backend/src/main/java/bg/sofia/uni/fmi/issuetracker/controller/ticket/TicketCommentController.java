package bg.sofia.uni.fmi.issuetracker.controller.ticket;

import bg.sofia.uni.fmi.issuetracker.dto.ticket.TicketCommentRequest;
import bg.sofia.uni.fmi.issuetracker.dto.ticket.TicketCommentResponse;
import bg.sofia.uni.fmi.issuetracker.service.contract.ticket.TicketCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects/{projectId}/tickets/{ticketId}/comments")
@Tag(name = "Ticket Comments", description = "Operations for managing ticket comments")
public class TicketCommentController {

    private final TicketCommentService ticketCommentService;

    public TicketCommentController(TicketCommentService ticketCommentService) {
        this.ticketCommentService = ticketCommentService;
    }

    @GetMapping
    @Operation(summary = "Get all comments", description = "Returns all comments in the form of a list")
    @ApiResponse(responseCode = "200", description = "List of comments")
    public ResponseEntity<?> getAllComments(
        @Parameter(description = "Uuid of the project", required = true)
        @PathVariable String projectId,
        @Parameter(description = "Uuid of the ticket", required = true)
        @PathVariable String ticketId) {

        return ResponseEntity.ok(ticketCommentService.
            getAllTicketCommentsByProjectAndTicket(projectId, ticketId));
    }

    @GetMapping("/{commentId}")
    @Operation(summary = "Get comment by UUID", description = "Returns a single ticket comment by its UUID")
    @ApiResponse(responseCode = "200", description = "Comment found")
    @ApiResponse(responseCode = "404", description = "Comment not found")
    public ResponseEntity<TicketCommentResponse> getTicketCommentByUuid(
        @Parameter(description = "Uuid of the project", required = true)
        @PathVariable String projectId,
        @Parameter(description = "Uuid of the ticket", required = true)
        @PathVariable String ticketId,
        @Parameter(description = "UUID of the comment", required = true)
        @PathVariable String commentId) {

        return ResponseEntity.ok(ticketCommentService.
            getTicketCommentByProjectAndTickedAndUuid(projectId, ticketId, commentId));
    }

    @PostMapping
    @Operation(summary = "Create comment", description = "Creates a new comment for a ticket")
    @ApiResponse(responseCode = "201", description = "Comment created")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<TicketCommentResponse> addTicketComment(
        @Parameter(description = "Uuid of the project", required = true)
        @PathVariable String projectId,
        @Parameter(description = "Uuid of the ticket", required = true)
        @PathVariable String ticketId,
        @Parameter(description = "Comment data", required = true)
        @RequestBody TicketCommentRequest ticketCommentRequest) {

        return ResponseEntity.status(HttpStatus.CREATED).body(ticketCommentService.
            addTicketCommentByProjectAndTicket(projectId, ticketId, ticketCommentRequest));
    }

    @PutMapping("/{commentId}")
    @Operation(summary = "Update comment", description = "Updates an existing comment by UUID")
    @ApiResponse(responseCode = "200", description = "Comment updated")
    @ApiResponse(responseCode = "404", description = "Comment not found")
    public ResponseEntity<TicketCommentResponse> updateTicketComment(
        @Parameter(description = "Uuid of the project", required = true)
        @PathVariable String projectId,
        @Parameter(description = "Uuid of the ticket", required = true)
        @PathVariable String ticketId,
        @Parameter(description = "Uuid of the comment", required = true)
        @PathVariable String commentId,
        @Parameter(description = "Updated comment data", required = true)
        @RequestBody TicketCommentRequest ticketCommentRequest) {

        return ResponseEntity.ok(ticketCommentService.
            updateTicketCommentByProjectAndTicket(projectId, ticketId, commentId, ticketCommentRequest));
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "Delete comment", description = "Deletes a comment by UUID")
    @ApiResponse(responseCode = "204", description = "Comment deleted")
    @ApiResponse(responseCode = "404", description = "Comment not found")
    public ResponseEntity<Void> deleteTicketComment(
        @Parameter(description = "Uuid of the project", required = true)
        @PathVariable String projectId,
        @Parameter(description = "Uuid of the ticket", required = true)
        @PathVariable String ticketId,
        @Parameter(description = "UUID of the comment", required = true)
        @PathVariable String commentId) {

        ticketCommentService.deleteTicketCommentByProjectAndTicket(projectId, ticketId, commentId);
        return ResponseEntity.noContent().build();
    }
}
