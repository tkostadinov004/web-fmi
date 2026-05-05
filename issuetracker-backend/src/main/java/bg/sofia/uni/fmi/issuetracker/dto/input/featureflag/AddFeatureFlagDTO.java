package bg.sofia.uni.fmi.issuetracker.dto.input.featureflag;

import bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record AddFeatureFlagDTO(
        @NotBlank(message = ValidationConstants.FeatureFlag.BLANK_NAME)
        @Length(max = 255, message = ValidationConstants.FeatureFlag.LENGTH_NAME)
        String name,
        String value) {
}
