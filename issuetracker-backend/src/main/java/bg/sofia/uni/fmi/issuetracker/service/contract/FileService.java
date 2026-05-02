package bg.sofia.uni.fmi.issuetracker.service.contract;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    void saveFile(MultipartFile file, String path);

    boolean fileExists(String path);

    Resource getFile(String path);
}
