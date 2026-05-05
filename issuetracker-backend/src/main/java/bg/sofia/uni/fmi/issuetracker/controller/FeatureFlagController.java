package bg.sofia.uni.fmi.issuetracker.controller;

import bg.sofia.uni.fmi.issuetracker.controller.common.RequireAdmin;
import bg.sofia.uni.fmi.issuetracker.dto.input.featureflag.AddFeatureFlagDTO;
import bg.sofia.uni.fmi.issuetracker.dto.input.featureflag.UpdateFeatureFlagDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.OutputFeatureFlagDTO;
import bg.sofia.uni.fmi.issuetracker.service.contract.FeatureFlagService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/featureflags")
public class FeatureFlagController {
    private final FeatureFlagService featureFlagService;

    public FeatureFlagController(FeatureFlagService featureFlagService) {
        this.featureFlagService = featureFlagService;
    }

    @GetMapping
    @RequireAdmin
    public ResponseEntity<List<OutputFeatureFlagDTO>> getAll() {
        return ResponseEntity.ok(featureFlagService.getAll());
    }

    @GetMapping("/{name}")
    @RequireAdmin
    public ResponseEntity<String> getValue(@PathVariable String name) {
        return ResponseEntity.ok(featureFlagService.getFeatureFlagValueSafe(name));
    }

    @PostMapping
    @RequireAdmin
    public ResponseEntity<Void> createFeatureFlag(@Valid @RequestBody AddFeatureFlagDTO dto) {
        featureFlagService.addFeatureFlag(dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{name}")
    @RequireAdmin
    public ResponseEntity<Void> editFeatureFlag(@PathVariable String name,
                                                @Valid @RequestBody UpdateFeatureFlagDTO dto) {
        featureFlagService.editFeatureFlagValue(name, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{name}")
    @RequireAdmin
    public ResponseEntity<Void> deleteFeatureFlag(@PathVariable String name) {
        featureFlagService.deleteFeatureFlag(name);
        return ResponseEntity.noContent().build();
    }
}
