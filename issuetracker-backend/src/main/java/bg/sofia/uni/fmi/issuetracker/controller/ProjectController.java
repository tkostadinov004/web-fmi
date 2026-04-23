package bg.sofia.uni.fmi.issuetracker.controller;

import bg.sofia.uni.fmi.issuetracker.controller.common.RequireRoles;
import bg.sofia.uni.fmi.issuetracker.model.auth.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects/{projectId}")
public class ProjectController {
    @GetMapping("/onlyForTeamLeads")
    @RequireRoles(roles = {Role.TEAM_LEAD, Role.ADMIN})
    public ResponseEntity<String> onlyForTeamLeads() {
        return ResponseEntity.ok("test");
    }

    @GetMapping("/forEveryone")
    public ResponseEntity<String> forEveryone() {
        return ResponseEntity.ok("test123");
    }
}
