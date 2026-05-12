package bg.sofia.uni.fmi.issuetracker.controller;

import bg.sofia.uni.fmi.issuetracker.controller.common.PaginationLinkHeader;
import bg.sofia.uni.fmi.issuetracker.dto.input.ticket.CreateTicketCommentDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.ticket.CreateTicketDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.ticket.UpdateTicketDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.ticket.TicketCommentDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.ticket.TicketDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.response.ErrorResponse;
import bg.sofia.uni.fmi.issuetracker.response.ValidationErrorResponse;
import bg.sofia.uni.fmi.issuetracker.service.contract.ticket.TicketCommentService;
import bg.sofia.uni.fmi.issuetracker.service.contract.ticket.TicketService;
import bg.sofia.uni.fmi.issuetracker.utils.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@Tag(name = "Ticket", description = "Endpoints for ticket management")
public class TicketController {
    private final TicketService ticketService;
    private final TicketCommentService ticketCommentService;

    public TicketController(TicketService ticketService, TicketCommentService ticketCommentService) {
        this.ticketService = ticketService;
        this.ticketCommentService = ticketCommentService;
    }

    @Operation(summary = "Get ticket details", description = "Retrieves detailed information for a specific ticket by its code.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ticket retrieved successfully",
            content = @Content(schema = @Schema(implementation = TicketDetailsDTO.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Ticket not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{code}")
    public ResponseEntity<TicketDetailsDTO> getTicketByCode(
        @Parameter(description = "Unique code identifier of the ticket", required = true) @PathVariable String code) {
        return ResponseEntity.ok(ticketService.getTicketByCode(code));
    }

    @Operation(summary = "Create a new ticket", description = "Creates a new ticket in the specified project with the provided details.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ticket created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid ticket data",
            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Project or assignee user not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Ticket with the given code already exists, or assignee is not part of the project",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<Void> createTicket(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Ticket creation data", required = true, content = @Content)
        @Valid @RequestBody CreateTicketDTO dto) {
        ticketService.createTicket(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Add dependent ticket", description = "Adds a new ticket as a dependent of a parent ticket. Both tickets must belong to the same project.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dependent ticket created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid ticket data",
            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Parent ticket, project, or assignee user not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Dependent ticket code already exists, assignee not part of project, or ticket not in project",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{parentCode}/dependents")
    public ResponseEntity<Void> addDependentTicket(
        @Parameter(description = "Code of the parent ticket", required = true) @PathVariable String parentCode,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dependent ticket creation data", required = true, content = @Content)
        @Valid @RequestBody CreateTicketDTO dto) {
        ticketService.addDependentTicketToTicket(parentCode, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Change ticket assignee", description = "Assigns or reassigns a ticket to a different user.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Assignee changed successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Ticket or assignee user not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{code}/assignee")
    public ResponseEntity<Void> changeAssignee(
        @Parameter(description = "Code of the ticket", required = true) @PathVariable String code,
        @Parameter(description = "Username of the new assignee", required = false) @RequestParam(required = false)
        String assigneeUsername) {
        ticketService.changeTicketAssignee(code, assigneeUsername);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update ticket information", description = "Updates ticket properties such as title, description, status, priority, and due date.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Ticket updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid update data",
            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Ticket not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{code}")
    public ResponseEntity<Void> updateTicket(
        @Parameter(description = "Code of the ticket to update", required = true) @PathVariable String code,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Ticket update data", required = true, content = @Content)
        @Valid @RequestBody UpdateTicketDTO dto) {
        ticketService.updateTicket(code, dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a ticket", description = "Removes a ticket from the system. This will also remove any associated dependencies and comments.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Ticket deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Ticket not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteTicket(
        @Parameter(description = "Code of the ticket to delete", required = true) @PathVariable String code) {
        ticketService.deleteTicket(code);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get ticket comments", description = "Retrieves paginated comments for a specific ticket.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comments retrieved successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = TicketCommentDetailsDTO.class)))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Ticket not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{code}/comments")
    public ResponseEntity<List<TicketCommentDetailsDTO>> getAllCommentsForTicket(
        @Parameter(description = "Code of the ticket", required = true) @PathVariable String code,
        @Parameter(description = "Page number starting at 1", required = false)
        @RequestParam(name = "page_number", required = false, defaultValue = Constants.DEFAULT_PAGE_NUMBER)
        Integer pageNumber,
        @Parameter(description = "Number of comments per page", required = false)
        @RequestParam(name = "page_size", required = false, defaultValue = Constants.DEFAULT_PAGE_SIZE)
        Integer pageSize,
        HttpServletRequest request) {
        Page<TicketCommentDetailsDTO> resultPage =
            ticketCommentService.getAllCommentsForTicket(code, pageNumber, pageSize);
        PaginationLinkHeader linkHeader =
            new PaginationLinkHeader(resultPage, request.getRequestURL().toString(), false);

        return ResponseEntity
            .ok()
            .header("Link", linkHeader.toString())
            .body(resultPage.toList());
    }

    @Operation(summary = "Add comment to ticket", description = "Creates a new comment on a ticket. The author is the currently authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Comment added successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid comment data",
            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Ticket or author user not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{code}/comments")
    public ResponseEntity<Void> addCommentToTicket(
        @Parameter(description = "Code of the ticket", required = true) @PathVariable String code,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Comment creation data", required = true, content = @Content)
        @Valid @RequestBody CreateTicketCommentDTO dto) {
        String author = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ticketCommentService.addTicketComment(author, code, dto);

        return ResponseEntity.noContent().build();
    }
}
