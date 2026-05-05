package bg.sofia.uni.fmi.issuetracker.response;

import java.util.Map;

public record ValidationErrorResponse(Map<String, String> errors) {
}