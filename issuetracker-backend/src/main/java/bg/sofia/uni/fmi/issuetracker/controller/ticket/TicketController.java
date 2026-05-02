package bg.sofia.uni.fmi.issuetracker.controller.ticket;

import bg.sofia.uni.fmi.issuetracker.dto.ticket.TicketRequest;
import bg.sofia.uni.fmi.issuetracker.dto.ticket.TicketResponse;
import bg.sofia.uni.fmi.issuetracker.service.contract.ticket.TicketService;
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
@RequestMapping("/projects/{projectId}/tickets")
@Tag(name = "Tickets", description = "Operations for managing the tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    @Operation(summary = "Get all tickets for a project")
    @ApiResponse(responseCode = "200", description = "List of tickets")
    public ResponseEntity<?> getAllTicketsByProject(
        @Parameter(description = "Project uuid", required = true)
        @PathVariable String projectId) {

        return ResponseEntity.ok(ticketService.getAllTicketsByProjectUuid(projectId));
    }

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
