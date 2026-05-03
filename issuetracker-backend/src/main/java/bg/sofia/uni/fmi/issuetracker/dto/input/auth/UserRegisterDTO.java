package bg.sofia.uni.fmi.issuetracker.dto.input.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import static bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants.Auth.BLANK_EMAIL;
import static bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants.Auth.BLANK_FIRST_NAME;
import static bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants.Auth.BLANK_LAST_NAME;
import static bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants.Auth.BLANK_PASSWORD;
import static bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants.Auth.BLANK_USERNAME;
import static bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants.Auth.INVALID_EMAIL;
import static bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants.Auth.LENGTH_COMPANY_NAME;
import static bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants.Auth.LENGTH_EMAIL;
import static bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants.Auth.LENGTH_FIRST_NAME;
import static bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants.Auth.LENGTH_LAST_NAME;
import static bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants.Auth.LENGTH_USERNAME;

public record UserRegisterDTO(
        @NotBlank(message = BLANK_FIRST_NAME) @Length(max = 200, message = LENGTH_FIRST_NAME) String firstName,
        @NotBlank(message = BLANK_LAST_NAME) @Length(max = 200, message = LENGTH_LAST_NAME) String lastName,
        @NotBlank(message = BLANK_USERNAME) @Length(max = 100, message = LENGTH_USERNAME) String username,
        @NotBlank(message = BLANK_EMAIL) @Email(message = INVALID_EMAIL) @Length(max = 255, message = LENGTH_EMAIL) String email,
        @Length(max = 200, message = LENGTH_COMPANY_NAME) String companyName,
        @NotBlank(message = BLANK_PASSWORD) String password) {
}