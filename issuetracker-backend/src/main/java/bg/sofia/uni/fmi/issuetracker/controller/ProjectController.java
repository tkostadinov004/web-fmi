package bg.sofia.uni.fmi.issuetracker.controller;

import bg.sofia.uni.fmi.issuetracker.dto.input.project.CreateProjectDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.project.CreateProjectUserDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.project.UpdateProjectDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.project.workflow.ProjectWorkflowDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.project.ProjectDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.project.ProjectDetailsUserDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.ticket.TicketDetailsDTO;
import bg.sofia.uni.fmi.issuetracker.response.ErrorResponse;
import bg.sofia.uni.fmi.issuetracker.response.ValidationErrorResponse;
import bg.sofia.uni.fmi.issuetracker.response.InvalidOrExpiredTokenErrorResponse;
import bg.sofia.uni.fmi.issuetracker.service.contract.ProjectService;
import bg.sofia.uni.fmi.issuetracker.service.contract.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/projects")
@Tag(name = "Project", description = "Endpoints for project-related operations")
public class ProjectController {
    private final TicketService ticketService;
    private final ProjectService projectService;

    public ProjectController(TicketService ticketService, ProjectService projectService) {
        this.ticketService = ticketService;
        this.projectService = projectService;
    }

    @Operation(summary = "Get all projects", description = "Retrieve all projects")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Projects retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProjectDetailsDTO.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
                    content = @Content(schema = @Schema(implementation = InvalidOrExpiredTokenErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<ProjectDetailsDTO>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @Operation(summary = "Get all tickets in a project", description = "Retrieves all tickets belonging to a specific project.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tickets retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TicketDetailsDTO.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
                    content = @Content(schema = @Schema(implementation = InvalidOrExpiredTokenErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Project not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{projectId}/tickets")
    public ResponseEntity<List<TicketDetailsDTO>> getAllTicketsByProject(
            @Parameter(description = "UUID of the project", required = true) @PathVariable String projectId) {
        return ResponseEntity.ok(ticketService.getAllTicketsByProject(projectId));
    }

    @Operation(summary = "Get project by UUID", description = "Retrieves a project by its UUID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Project retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProjectDetailsDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
                    content = @Content(schema = @Schema(implementation = InvalidOrExpiredTokenErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
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
                    responseCode = "200", description = "Project created successfully",
                    content = @Content(schema = @Schema(implementation = ProjectDetailsDTO.class))),
            @ApiResponse(
                    responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
                    content = @Content(schema = @Schema(implementation = InvalidOrExpiredTokenErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ProjectDetailsDTO> createProject(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Project creation data", required = true, content = @Content)
            @Valid @RequestBody
            CreateProjectDTO dto) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(projectService.addProject(dto, username));
    }

    @Operation(summary = "Update project", description = "Updates an existing project.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204", description = "Project updated successfully",
                    content = @Content),
            @ApiResponse(
                    responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
                    content = @Content(schema = @Schema(implementation = InvalidOrExpiredTokenErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "404", description = "Project not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{projectId}")
    public ResponseEntity<Void> updateProject(
            @Parameter(description = "UUID of the project", required = true)
            @PathVariable String projectId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Project update data", required = true, content = @Content)
            @Valid @RequestBody UpdateProjectDTO dto
    ) {
        projectService.updateProject(projectId, dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete project", description = "Deletes a project by UUID.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204", description = "Project deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
                    content = @Content(schema = @Schema(implementation = InvalidOrExpiredTokenErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "404", description = "Project not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(
            @Parameter(description = "Code of the project to delete", required = true) @PathVariable String projectId) {

        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all users in project",
            description = "Retrieves all users that are part of the specified project.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Project users retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProjectDetailsUserDTO.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
                    content = @Content(schema = @Schema(implementation = InvalidOrExpiredTokenErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "404", description = "Project not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{projectId}/users")
    public ResponseEntity<List<ProjectDetailsUserDTO>> getAssignedUsers(
            @Parameter(description = "UUID of the project", required = true)
            @PathVariable String projectId) {

        return ResponseEntity.ok(projectService.getProjectUsers(projectId));
    }

    @Operation(summary = "Add user to project",
            description = "Adds a user to the specified project.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201", description = "User added to project successfully",
                    content = @Content(schema = @Schema(implementation = ProjectDetailsUserDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid data",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
                    content = @Content(schema = @Schema(implementation = InvalidOrExpiredTokenErrorResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404", description = "Project or user not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409", description = "User already part of the project",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/{projectId}/users")
    public ResponseEntity<ProjectDetailsUserDTO> addUserToProject(
            @Parameter(description = "UUID of the project", required = true)
            @PathVariable String projectId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Project user creation data", required = true, content = @Content)
            @Valid @RequestBody CreateProjectUserDTO dto
    ) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(projectService.addProjectUser(projectId, dto, username));
    }

    @Operation(summary = "Remove user from project", description = "Removes a user from the specified project.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204", description = "User removed from project successfully",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
                    content = @Content(schema = @Schema(implementation = InvalidOrExpiredTokenErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "404", description = "Project or user not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{projectId}/users/{username}")
    public ResponseEntity<Void> removeUserFromProject(
            @Parameter(description = "UUID of the project", required = true)
            @PathVariable String projectId,
            @Parameter(description = "Username of the user", required = true)
            @PathVariable String username
    ) {
        String initiatorUsername = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        projectService.deleteProjectUser(projectId, username, initiatorUsername);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{projectId}/workflow")
    @Operation(summary = "Get project workflow", description = "Gets the workflow for a project.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Workflow returned successfully",
                    content = @Content(schema = @Schema(implementation = ProjectWorkflowDTO.class))),
            @ApiResponse(responseCode = "404", description = "Project not found or workflow for the given project doesn't exist",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProjectWorkflowDTO> getWorkflow(@Parameter(description = "UUID of the project", required = true) @PathVariable String projectId) {
        return ResponseEntity.ok(projectService.getProjectWorkflow(projectId));
    }

    @PostMapping("/{projectId}/workflow")
    @Operation(summary = "Create or replace project workflow", description = "Creates a new workflow for the project. Validates statuses and transitions.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Workflow created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid workflow definition",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Project not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Workflow already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> createWorkflow(@Parameter(description = "UUID of the project", required = true) @PathVariable String projectId, @RequestBody ProjectWorkflowDTO dto) {
        projectService.addProjectWorkflow(projectId, dto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{projectId}/workflow")
    @Operation(summary = "Replace project workflow", description = "Replaces the existing workflow for the project with the provided definition.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Workflow replaced successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid workflow definition",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Project not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> replaceWorkflow(@Parameter(description = "UUID of the project", required = true) @PathVariable String projectId, @RequestBody ProjectWorkflowDTO dto) {
        projectService.deleteProjectWorkflow(projectId);
        projectService.addProjectWorkflow(projectId, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{projectId}/workflow")
    @Operation(summary = "Delete project workflow", description = "Deletes the workflow associated with the project.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Workflow deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Project not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteWorkflow(@Parameter(description = "UUID of the project", required = true) @PathVariable String projectId) {
        projectService.deleteProjectWorkflow(projectId);
        return ResponseEntity.noContent().build();
    }
}