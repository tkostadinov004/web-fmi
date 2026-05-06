package bg.sofia.uni.fmi.issuetracker.controller;

import bg.sofia.uni.fmi.issuetracker.controller.common.RequireAdmin;
import bg.sofia.uni.fmi.issuetracker.dto.input.featureflag.AddFeatureFlagDTO;
import bg.sofia.uni.fmi.issuetracker.dto.output.OutputFeatureFlagDTO;
import bg.sofia.uni.fmi.issuetracker.response.ErrorResponse;
import bg.sofia.uni.fmi.issuetracker.response.ValidationErrorResponse;
import bg.sofia.uni.fmi.issuetracker.service.contract.FeatureFlagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/featureflags")
@Tag(name = "Feature Flag", description = "Endpoints for managing feature flags")
public class FeatureFlagController {
    private final FeatureFlagService featureFlagService;

    public FeatureFlagController(FeatureFlagService featureFlagService) {
        this.featureFlagService = featureFlagService;
    }

    @Operation(summary = "List all feature flags", description = "Retrieves all configured feature flags.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Feature flags retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OutputFeatureFlagDTO.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "The current user does not have admin privileges",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    @RequireAdmin
    public ResponseEntity<List<OutputFeatureFlagDTO>> getAll() {
        return ResponseEntity.ok(featureFlagService.getAll());
    }

    @Operation(summary = "Get feature flag value", description = "Retrieves the current value of the specified feature flag.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Feature flag value retrieved successfully",
                    content = @Content(schema = @Schema(implementation = OutputFeatureFlagDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "The current user does not have admin privileges",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Feature flag not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{name}")
    @RequireAdmin
    public ResponseEntity<OutputFeatureFlagDTO> getValue(@Parameter(description = "Name of the feature flag", required = true) @PathVariable String name) {
        return ResponseEntity.ok(featureFlagService.getFeatureFlag(name));
    }

    @Operation(summary = "Create a feature flag", description = "Creates a new feature flag with the provided name and value.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Feature flag created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid feature flag data",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "The current user does not have admin privileges",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "A feature flag with the given name already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    @RequireAdmin
    public ResponseEntity<Void> createFeatureFlag(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Feature flag creation data", required = true, content = @Content) @Valid @RequestBody AddFeatureFlagDTO dto) {
        featureFlagService.addFeatureFlag(dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update a feature flag", description = "Updates the value of an existing feature flag.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Feature flag updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid updated feature flag data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "The current user does not have admin privileges",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Feature flag not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{name}")
    @RequireAdmin
    public ResponseEntity<Void> setFeatureFlagValue(@Parameter(description = "Name of the feature flag", required = true) @PathVariable String name,
                                                    @Parameter(description = "The new value for the feature flag", required = true) @RequestParam(name = "new_value") boolean new_value) {
        featureFlagService.setFeatureFlagValue(name, new_value);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a feature flag", description = "Deletes the specified feature flag.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Feature flag deleted successfully", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing auth credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "The current user does not have admin privileges",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Feature flag not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{name}")
    @RequireAdmin
    public ResponseEntity<Void> deleteFeatureFlag(@Parameter(description = "Name of the feature flag", required = true) @PathVariable String name) {
        featureFlagService.deleteFeatureFlag(name);
        return ResponseEntity.noContent().build();
    }
}
