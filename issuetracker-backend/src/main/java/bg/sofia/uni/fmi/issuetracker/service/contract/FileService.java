package bg.sofia.uni.fmi.issuetracker.service.contract;

import bg.sofia.uni.fmi.issuetracker.exception.file.FileServiceException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileService {
    /**
     * Saves or replaces the provided multipart file at the specified relative path.
     *
     * <p>The provided file is stored under the configured root directory. Any
     * necessary parent directories are created and existing content is overwritten if the file already exists.</p>
     *
     * @param file the uploaded file to store
     * @param path the relative path under the service root where the file should be saved
     * @throws FileServiceException if the file is empty, the path is outside the configured root, or the file cannot be written
     */
    void saveOrReplaceFile(MultipartFile file, Path path);

    /**
     * Checks whether a file exists at the given filesystem path.
     *
     * @param path the filesystem path to check
     * @return {@code true} if a file exists at the given path, {@code false} otherwise
     */
    boolean fileExists(String path);

    /**
     * Loads the requested file as a Spring {@link Resource}.
     *
     * @param path the filesystem path of the file to load
     * @return the loaded file as a {@link Resource}
     * @throws FileServiceException if the file is not readable or the path cannot be resolved
     */
    Resource getFile(String path);
}
