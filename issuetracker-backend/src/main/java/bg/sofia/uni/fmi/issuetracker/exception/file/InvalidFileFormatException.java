package bg.sofia.uni.fmi.issuetracker.exception.file;

public class InvalidFileFormatException extends FileServiceException {
    public InvalidFileFormatException(String message) {
        super(message);
    }

    public InvalidFileFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
