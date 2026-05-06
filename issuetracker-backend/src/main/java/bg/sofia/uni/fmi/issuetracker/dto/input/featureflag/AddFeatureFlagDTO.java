package bg.sofia.uni.fmi.issuetracker.dto.input.featureflag;

import bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

@Schema(description = "Request body for creating a new feature flag.")
public record AddFeatureFlagDTO(
        @NotBlank(message = ValidationConstants.FeatureFlag.BLANK_NAME)
        @Length(max = 255, message = ValidationConstants.FeatureFlag.LENGTH_NAME)
        @Schema(description = "Unique name of the feature flag. Must not be blank and must be at most 255 characters.", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1, maxLength = 255)
        String name,
        @NotNull(message = ValidationConstants.FeatureFlag.NULL_VALUE)
        @Pattern(regexp = "(true)|(false)", message = ValidationConstants.FeatureFlag.INVALID_VALUE)
        @Schema(description = "Value of the feature flag. Defaults to false if not provided.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String value) {

    public boolean valueAsBoolean() {
        return Boolean.parseBoolean(value);
    }
} 
