package bg.sofia.uni.fmi.issuetracker.controller.ticket;

import bg.sofia.uni.fmi.issuetracker.dto.ticket.TicketRequest;
import bg.sofia.uni.fmi.issuetracker.dto.ticket.TicketResponse;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketPriority;
import bg.sofia.uni.fmi.issuetracker.model.ticket.TicketStatus;
import bg.sofia.uni.fmi.issuetracker.service.contract.ticket.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.PriorityOrdered;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}/tickets")
@Tag(name = "Tickets", description = "Operations for managing the tickets")
public class TicketController {

    private final TicketService ticketService;
    private final PriorityOrdered priorityOrdered;

    public TicketController(TicketService ticketService, PriorityOrdered priorityOrdered) {
        this.ticketService = ticketService;
        this.priorityOrdered = priorityOrdered;
    }

//    @GetMapping
//    @Operation(summary = "Get all tickets for a project")
//    @ApiResponse(responseCode = "200", description = "List of tickets")
//    public ResponseEntity<?> getAllTicketsByProject(
//        @Parameter(description = "Project uuid", required = true)
//        @PathVariable String projectId) {
//
//        return ResponseEntity.ok(ticketService.getAllTicketsByProjectUuid(projectId));
//    }

    @GetMapping("/{ticketId}")
    @Operation(summary = "Get ticket by UUID", description = "Returns a single ticket by its UUID")
    @ApiResponse(responseCode = "200", description = "Ticket found")
    @ApiResponse(responseCode = "404", description = "Ticket not found")
    public ResponseEntity<TicketResponse> getTicket(
        @Parameter(description = "Project uuid)", required = true)
        @PathVariable String projectId,
        @Parameter(description = "Ticket uuid", required = true)
        @PathVariable String ticketId) {
        return ResponseEntity.ok(ticketService.getTicketByProjectUuidAndTicketUuid(projectId, ticketId).get());
    }

    @GetMapping
    @Operation(summary = "List tickets", description = "Search tickets with optional ticketStatus," +
        " ticketPriority, assignee username")
    @ApiResponse(responseCode = "200", description = "Tickets successfully retrieved")
    @ApiResponse(responseCode = "404", description = "Project not found")
    public ResponseEntity<List<TicketResponse>> getTickets(
        @Parameter(description = "Project UUID", required = true)
        @PathVariable String projectId,
        @Parameter(description = "Ticket status", required = false)
        @RequestParam(required = false) TicketStatus ticketStatus,
        @Parameter(description = "Ticket priority", required = false)
        @RequestParam(required = false) TicketPriority ticketPriority,
        @Parameter(description = "Assignee username", required = false)
        @RequestParam(required = false) String assigneeUsername) {

        return ResponseEntity.ok(
            ticketService.getAllTicketsByProjectUuidStatusPriorityAndAssigneeUsername(projectId,
                ticketStatus, ticketPriority, assigneeUsername));
    }

    @PostMapping
    @Operation(summary = "Create ticket in project")
    @ApiResponse(responseCode = "201", description = "Ticket created")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<TicketResponse> addTicket(
        @Parameter(description = "Project UUID", required = true)
        @PathVariable String projectId,
        @Parameter(description = "Ticket data", required = true)
        @RequestBody TicketRequest ticket) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ticketService.addTicketByProjectUuid(projectId, ticket));
    }

    @PutMapping("/{ticketId}")
    @Operation(summary = "Update ticket")
    @ApiResponse(responseCode = "200", description = "Ticket updated")
    @ApiResponse(responseCode = "404", description = "Ticket not found")
    public ResponseEntity<TicketResponse> updateTicket(
        @Parameter(description = "Project UUID", required = true)
        @PathVariable String projectId,
        @Parameter(description = "Ticket UUID", required = true)
        @PathVariable String ticketId,
        @Parameter(description = "Updated ticket data", required = true)
        @RequestBody TicketRequest ticket) {

        return ResponseEntity.ok(ticketService.updateTicketByProjectUuid(projectId, ticketId, ticket));
    }

    @DeleteMapping("/{ticketId}")
    @Operation(summary = "Delete ticket")
    @ApiResponse(responseCode = "204", description = "Ticket deleted")
    @ApiResponse(responseCode = "404", description = "Ticket not found")
    public ResponseEntity<Void> deleteTicket(
        @Parameter(description = "Project UUID", required = true)
        @PathVariable String projectId,
        @Parameter(description = "Ticket UUID", required = true)
        @PathVariable String ticketId) {

        ticketService.deleteTicketByProjectUuid(projectId, ticketId);
        return ResponseEntity.noContent().build();
    }
}
