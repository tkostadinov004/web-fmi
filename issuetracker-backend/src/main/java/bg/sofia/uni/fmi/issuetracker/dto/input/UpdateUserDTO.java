package bg.sofia.uni.fmi.issuetracker.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.Length;

import static bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants.Auth.LENGTH_COMPANY_NAME;
import static bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants.Auth.LENGTH_EMAIL;
import static bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants.Auth.LENGTH_FIRST_NAME;
import static bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants.Auth.LENGTH_LAST_NAME;

@Schema(description = "Request body for updating the current user's profile data.")
public record UpdateUserDTO(
        @Length(max = 200, message = LENGTH_FIRST_NAME)
        @Schema(description = "Updated first name. Optional, maximum 200 characters.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 200)
        String firstName,

        @Length(max = 200, message = LENGTH_LAST_NAME)
        @Schema(description = "Updated last name. Optional, maximum 200 characters.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 200)
        String lastName,

        @Length(max = 255, message = LENGTH_EMAIL)
        @Schema(description = "Updated email address. Optional, maximum 255 characters.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 255)
        String email,

        @Length(max = 200, message = LENGTH_COMPANY_NAME)
        @Schema(description = "Updated company name. Optional, maximum 200 characters.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 200)
        String companyName) {
}
