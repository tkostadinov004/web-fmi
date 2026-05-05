package bg.sofia.uni.fmi.issuetracker.dto.output;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response body containing a feature flag name and value.")
public record OutputFeatureFlagDTO(
        @Schema(description = "Unique name of the feature flag.", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
        @Schema(description = "Current value of the feature flag.", requiredMode = Schema.RequiredMode.REQUIRED)
        boolean value) {
} 
