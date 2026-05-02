package bg.sofia.uni.fmi.issuetracker.dto.input.auth;

import bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRegisterDTO(@NotBlank(message = ValidationConstants.Auth.BLANK_FIRST_NAME) String firstName,
                              @NotBlank(message = ValidationConstants.Auth.BLANK_LAST_NAME) String lastName,
                              @NotBlank(message = ValidationConstants.Auth.BLANK_USERNAME) String username,
                              @NotBlank(message = ValidationConstants.Auth.BLANK_EMAIL) @Email(message = ValidationConstants.Auth.INVALID_EMAIL) String email,
                              String companyName,
                              @NotBlank(message = ValidationConstants.Auth.BLANK_PASSWORD) String password) {
}