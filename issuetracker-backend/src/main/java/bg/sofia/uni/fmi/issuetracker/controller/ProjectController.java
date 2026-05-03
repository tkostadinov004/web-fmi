package bg.sofia.uni.fmi.issuetracker.controller;

import bg.sofia.uni.fmi.issuetracker.controller.common.RequireRoles;
import bg.sofia.uni.fmi.issuetracker.model.auth.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects/{projectId}")
@Tag(name = "Project", description = "Project membership and access control endpoints")
public class ProjectController {
    @Operation(summary = "Access endpoint for project team leads", description = "Returns content only if the authenticated user has the TEAM_LEAD role for the specified project.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Access granted for team lead"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials"),
            @ApiResponse(responseCode = "403", description = "The user does not have the required role for the project"),
            @ApiResponse(responseCode = "404", description = "Project not found or user not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @GetMapping("/onlyForTeamLeads")
    @RequireRoles(roles = {Role.TEAM_LEAD})
    public ResponseEntity<String> onlyForTeamLeads() {
        return ResponseEntity.ok("test");
    }

    @Operation(summary = "Access endpoint for all project members", description = "Returns content for any authenticated user within the specified project.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Access granted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials"),
            @ApiResponse(responseCode = "404", description = "Project not found or user not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @GetMapping("/forEveryone")
    public ResponseEntity<String> forEveryone() {
        return ResponseEntity.ok("test123");
    }
}
