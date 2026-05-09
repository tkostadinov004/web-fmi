package bg.sofia.uni.fmi.issuetracker.dto.input.file;

import bg.sofia.uni.fmi.issuetracker.utils.messages.ValidationConstants;
import jakarta.validation.constraints.NotBlank;

public record FetchFileDTO(@NotBlank(message = ValidationConstants.File.BLANK_PATH) String path) {
}
