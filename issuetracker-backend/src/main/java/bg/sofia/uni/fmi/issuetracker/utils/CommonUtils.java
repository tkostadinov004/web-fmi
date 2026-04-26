package bg.sofia.uni.fmi.issuetracker.utils;

import tools.jackson.databind.ObjectMapper;

import java.util.Map;

public class CommonUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    public static String buildErrorResponse(String message) {
        return OBJECT_MAPPER.writeValueAsString(Map.of("message", message));
    }
}
