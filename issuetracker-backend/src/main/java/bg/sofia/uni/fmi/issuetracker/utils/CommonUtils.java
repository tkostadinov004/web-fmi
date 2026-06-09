package bg.sofia.uni.fmi.issuetracker.utils;

import bg.sofia.uni.fmi.issuetracker.response.ErrorResponse;
import bg.sofia.uni.fmi.issuetracker.response.InvalidOrExpiredTokenErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import static bg.sofia.uni.fmi.issuetracker.utils.Constants.VALID_IMAGE_FORMATS;

public class CommonUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static ErrorResponse buildErrorResponse(String message) {
        return new ErrorResponse(message);
    }

    public static InvalidOrExpiredTokenErrorResponse buildErrorResponse(String message, boolean isExpired) {
        return new InvalidOrExpiredTokenErrorResponse(message, isExpired);
    }

    public static String buildErrorResponseAsJson(String message, boolean isExpired) {
        try {
            return OBJECT_MAPPER.writeValueAsString(buildErrorResponse(message, isExpired));
        } catch (JsonProcessingException e) {
            return "{\"error\":\"" + message + "\"}";
        }
    }

    public static boolean isImageFile(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        return VALID_IMAGE_FORMATS.contains(extension);
    }

    public static boolean isValidFilename(String filename) {
        File f = new File(filename);
        try {
            return f.getCanonicalFile().getName().equals(filename);
        } catch (IOException e) {
            return false;
        }
    }
}
