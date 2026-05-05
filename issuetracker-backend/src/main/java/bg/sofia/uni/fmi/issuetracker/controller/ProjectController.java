package bg.sofia.uni.fmi.issuetracker.controller;

import bg.sofia.uni.fmi.issuetracker.dto.output.ticket.TicketDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.service.contract.ticket.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}")
@Tag(name = "Project", description = "Endpoints for project-related operations")
public class ProjectController {
    private final TicketService ticketService;

    public ProjectController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Operation(summary = "Get all tickets in a project", description = "Retrieves all tickets belonging to a specific project.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tickets retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @GetMapping("/tickets")
    public ResponseEntity<List<TicketDetailsDTO>> getAllTicketsByProject(@Parameter(description = "UUID of the project", required = true) @PathVariable String projectId) {
        return ResponseEntity.ok(ticketService.getAllTicketsByProject(projectId));
    }
}
