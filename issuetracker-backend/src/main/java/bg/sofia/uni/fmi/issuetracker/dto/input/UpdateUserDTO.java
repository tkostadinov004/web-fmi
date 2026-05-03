package bg.sofia.uni.fmi.issuetracker.dto.input;

import org.hibernate.validator.constraints.Length;

import static bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants.Auth.LENGTH_COMPANY_NAME;
import static bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants.Auth.LENGTH_EMAIL;
import static bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants.Auth.LENGTH_FIRST_NAME;
import static bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants.Auth.LENGTH_LAST_NAME;

public record UpdateUserDTO(
        @Length(max = 200, message = LENGTH_FIRST_NAME) String firstName,
        @Length(max = 200, message = LENGTH_LAST_NAME) String lastName,
        @Length(max = 255, message = LENGTH_EMAIL) String email,
        @Length(max = 200, message = LENGTH_COMPANY_NAME) String companyName) {
}
