package bg.sofia.uni.fmi.issuetracker.dto.input.featureflag;

import bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@Schema(description = "Request body for creating a new feature flag.")
public record AddFeatureFlagDTO(
        @NotBlank(message = ValidationConstants.FeatureFlag.BLANK_NAME)
        @Length(max = 255, message = ValidationConstants.FeatureFlag.LENGTH_NAME)
        @Schema(description = "Unique name of the feature flag. Must not be blank and must be at most 255 characters.", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1, maxLength = 255)
        String name,
        @Schema(description = "Value of the feature flag. Optional.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String value) {
} 
