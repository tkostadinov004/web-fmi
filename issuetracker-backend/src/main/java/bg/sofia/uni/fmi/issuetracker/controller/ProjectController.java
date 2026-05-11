package bg.sofia.uni.fmi.issuetracker.controller;

import bg.sofia.uni.fmi.issuetracker.dto.input.project.CreateProjectDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.project.UpdateProjectDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.project.ProjectDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.ticket.TicketDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.response.ErrorResponse;
import bg.sofia.uni.fmi.issuetracker.service.contract.ProjectService;
import bg.sofia.uni.fmi.issuetracker.service.contract.ticket.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}")
@Tag(name = "Project", description = "Endpoints for project-related operations")
public class ProjectController {

    private final TicketService ticketService;
    private final ProjectService projectService;

    public ProjectController(TicketService ticketService, ProjectService projectService) {
        this.ticketService = ticketService;
        this.projectService = projectService;
    }

    @Operation(summary = "Get all tickets in a project", description = "Retrieves all tickets belonging to a specific project.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tickets retrieved successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = TicketDetailsDTO.class)))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Project not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/tickets")
    public ResponseEntity<List<TicketDetailsDTO>> getAllTicketsByProject(
        @Parameter(description = "UUID of the project", required = true) @PathVariable String projectId) {
        return ResponseEntity.ok(ticketService.getAllTicketsByProject(projectId));
    }

    @Operation(summary = "Get all projects", description = "Retrieve all projects")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Projects retrieved successfully",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProjectDetailsDTO.class)))),
        @ApiResponse(responseCode = "500", description = "Unexpected server error",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<ProjectDetailsDTO>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @Operation(summary = "Get project by UUID", description = "Retrieves a project by its UUID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Project retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProjectDetailsDTO.class))),
        @ApiResponse(
            responseCode = "404", description = "Project not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "500", description = "Unexpected server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDetailsDTO> getProjectByProjectId(
        @Parameter(description = "UUID of the project", required = true)
        @PathVariable String projectId) {

        return ResponseEntity.ok(projectService.findProjectById(projectId));
    }

    @Operation(summary = "Create project", description = "Creates a new project.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "201", description = "Project created successfully",
            content = @Content(schema = @Schema(implementation = ProjectDetailsDTO.class))),
        @ApiResponse(
            responseCode = "400", description = "Invalid request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "500", description = "Unexpected server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ProjectDetailsDTO> createProject(
        @Valid @RequestBody CreateProjectDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(projectService.addProject(dto));
    }

    @Operation(summary = "Update project", description = "Updates an existing project.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", description = "Project updated successfully",
            content = @Content(schema = @Schema(implementation = ProjectDetailsDTO.class))),
        @ApiResponse(
            responseCode = "404", description = "Project not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "400", description = "Invalid request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "500", description = "Unexpected server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectDetailsDTO> updateProject(
        @Parameter(description = "UUID of the project", required = true)
        @PathVariable String projectId,
        @Valid @RequestBody UpdateProjectDTO dto
    ) {
        projectService.updateProject(projectId, dto);
        return ResponseEntity.noContent().build();
    }
}
