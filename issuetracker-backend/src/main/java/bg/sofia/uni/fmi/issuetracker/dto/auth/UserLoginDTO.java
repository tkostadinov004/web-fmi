package bg.sofia.uni.fmi.issuetracker.dto.auth;

import bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants;
import jakarta.validation.constraints.NotBlank;

public record UserLoginDTO(@NotBlank(message = ValidationConstants.Auth.BLANK_USERNAME) String username,
                              @NotBlank(message = ValidationConstants.Auth.BLANK_PASSWORD) String password) {
}

