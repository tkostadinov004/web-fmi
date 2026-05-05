package bg.sofia.uni.fmi.issuetracker.utils;

import bg.sofia.uni.fmi.issuetracker.response.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CommonUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static ErrorResponse buildErrorResponse(String message) {
        return new ErrorResponse(message);
    }

    public static String buildErrorResponseAsJson(String message) {
        try {
            return OBJECT_MAPPER.writeValueAsString(buildErrorResponse(message));
        } catch (JsonProcessingException e) {
            return "{\"message\":\"" + message + "\"}";
        }
    }
}
