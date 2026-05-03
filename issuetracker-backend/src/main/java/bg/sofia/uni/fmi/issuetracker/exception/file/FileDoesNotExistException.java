package bg.sofia.uni.fmi.issuetracker.exception.file;

public class FileDoesNotExistException extends FileServiceException {
    public FileDoesNotExistException(String message) {
        super(message);
    }

    public FileDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
