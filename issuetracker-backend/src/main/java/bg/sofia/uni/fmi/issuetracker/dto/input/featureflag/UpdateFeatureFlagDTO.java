package bg.sofia.uni.fmi.issuetracker.dto.input.featureflag;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request body for updating an existing feature flag.")
public record UpdateFeatureFlagDTO(
        @Schema(description = "New value for the feature flag. Optional.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String value) {
} 
