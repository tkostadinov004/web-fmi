package bg.sofia.uni.fmi.issuetracker.controller;

import bg.sofia.uni.fmi.issuetracker.dto.input.ticket.UpdateTicketCommentDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.ticket.TicketCommentDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.response.ErrorResponse;
import bg.sofia.uni.fmi.issuetracker.response.ValidationErrorResponse;
import bg.sofia.uni.fmi.issuetracker.response.InvalidOrExpiredTokenErrorResponse;
import bg.sofia.uni.fmi.issuetracker.service.contract.TicketCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comments")
@Tag(name = "Ticket Comment", description = "Endpoints for ticket comment management")
public class TicketCommentController {
    private final TicketCommentService ticketCommentService;

    public TicketCommentController(TicketCommentService ticketCommentService) {
        this.ticketCommentService = ticketCommentService;
    }

    @Operation(summary = "Get a comment", description = "Retrieves the details of a specific ticket comment by its UUID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comment retrieved successfully",
                    content = @Content(schema = @Schema(implementation = TicketCommentDetailsDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
                    content = @Content(schema = @Schema(implementation = InvalidOrExpiredTokenErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Comment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{commentId}")
    public ResponseEntity<TicketCommentDetailsDTO> getComment(@Parameter(description = "UUID of the comment", required = true) @PathVariable String commentId) {
        return ResponseEntity.ok(ticketCommentService.getTicketComment(commentId));
    }

    @Operation(summary = "Update a comment", description = "Updates the content of an existing ticket comment.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Comment updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid update data",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
                    content = @Content(schema = @Schema(implementation = InvalidOrExpiredTokenErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "User tried updating someone else's comment",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Comment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{commentId}")
    public ResponseEntity<Void> updateTicketComment(@Parameter(description = "UUID of the comment to update", required = true) @PathVariable String commentId,
                                                    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Comment update data", required = true, content = @Content) @Valid @RequestBody UpdateTicketCommentDTO dto) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ticketCommentService.updateTicketComment(commentId, dto, username);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a comment", description = "Removes a comment from a ticket.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Comment deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
                    content = @Content(schema = @Schema(implementation = InvalidOrExpiredTokenErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "User tried deleting someone else's comment",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Comment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteTicketComment(@Parameter(description = "UUID of the comment to delete", required = true) @PathVariable String commentId) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ticketCommentService.deleteTicketComment(commentId, username);
        return ResponseEntity.noContent().build();
    }
}