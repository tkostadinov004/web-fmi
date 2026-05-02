package bg.sofia.uni.fmi.issuetracker.service.contract;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileService {
    void saveOrReplaceFile(MultipartFile file, Path path);

    boolean fileExists(String path);

    Resource getFile(String path);
}
