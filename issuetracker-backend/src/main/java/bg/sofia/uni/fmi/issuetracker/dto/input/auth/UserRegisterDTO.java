package bg.sofia.uni.fmi.issuetracker.dto.input.auth;

import io.swagger.v3.oas.annotations.media.Schema;
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

@Schema(description = "Request body for registering a new user.")
public record UserRegisterDTO(
        @NotBlank(message = BLANK_FIRST_NAME)
        @Length(max = 200, message = LENGTH_FIRST_NAME)
        @Schema(description = "First name. Must not be blank and must be at most 200 characters.", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1, maxLength = 200)
        String firstName,
        @NotBlank(message = BLANK_LAST_NAME)
        @Length(max = 200, message = LENGTH_LAST_NAME)
        @Schema(description = "Last name. Must not be blank and must be at most 200 characters.", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1, maxLength = 200)
        String lastName,
        @NotBlank(message = BLANK_USERNAME)
        @Length(max = 100, message = LENGTH_USERNAME)
        @Schema(description = "Username. Must not be blank and must be at most 100 characters.", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1, maxLength = 100)
        String username,
        @NotBlank(message = BLANK_EMAIL)
        @Email(message = INVALID_EMAIL)
        @Length(max = 255, message = LENGTH_EMAIL)
        @Schema(description = "Email address. Must not be blank, must be a valid email format, and must be at most 255 characters.", requiredMode = Schema.RequiredMode.REQUIRED, format = "email", minLength = 1, maxLength = 255)
        String email,
        @Length(max = 200, message = LENGTH_COMPANY_NAME)
        @Schema(description = "Company name. Optional, with a maximum length of 200 characters.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = 200)
        String companyName,
        @NotBlank(message = BLANK_PASSWORD)
        @Schema(description = "Password. Must not be blank.", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1)
        String password) {
}