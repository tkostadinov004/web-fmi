package bg.sofia.uni.fmi.issuetracker.controller;

import bg.sofia.uni.fmi.issuetracker.controller.common.RequireTeamLead;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects/{projectId}")
public class ProjectController {
    @GetMapping("/onlyForTeamLeads")
    @RequireTeamLead
    public ResponseEntity<String> onlyForTeamLeads(@PathVariable String projectId) {
        return ResponseEntity.ok("test");
    }

    @GetMapping("/forEveryone")
    public ResponseEntity<String> forEveryone(@PathVariable String projectId) {
        return ResponseEntity.ok("test123");
    }
}
