package bg.sofia.uni.fmi.issuetracker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects/{projectId}")
public class ProjectController {

    @GetMapping("/onlyForTeamLeads")
    @PreAuthorize("@projectService.hasRole(authentication, #projectId, 'TEAM_LEAD')")
    public ResponseEntity<String> onlyForTeamLeads(@PathVariable String projectId) {
        return ResponseEntity.ok("test");
    }

    @GetMapping("/forEveryone")
    public ResponseEntity<String> forEveryone(@PathVariable String projectId) {
        return ResponseEntity.ok("test123");
    }
}
