package bg.sofia.uni.fmi.issuetracker.service;

import bg.sofia.uni.fmi.issuetracker.exception.file.FileServiceException;
import bg.sofia.uni.fmi.issuetracker.service.contract.FileService;
import bg.sofia.uni.fmi.issuetracker.utils.CommonUtils;
import bg.sofia.uni.fmi.issuetracker.utils.FileServiceRoot;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    private final FileServiceRoot root;

    public FileServiceImpl(FileServiceRoot root) {
        this.root = root;
    }

    @Override
    public String saveOrReplaceFile(MultipartFile file, Path targetDirectory, String customName) {
        if (file.isEmpty()) {
            throw new FileServiceException(ExceptionMessages.File.emptyFile());
        }
        if (customName != null && !CommonUtils.isValidFilename(customName)) {
            throw new FileServiceException(ExceptionMessages.File.invalidCustomName());
        }

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String filename = (customName != null ? customName : UUID.randomUUID()) + "." + extension;

        Path destinationFile = root.getRoot().resolve(targetDirectory).resolve(filename).normalize().toAbsolutePath();
        if (!destinationFile.getParent().startsWith(root.getRoot().toAbsolutePath())) {
            throw new FileServiceException(ExceptionMessages.File.unableToStoreOutsideOfRoot());
        }
        try (InputStream inputStream = file.getInputStream()) {
            Files.createDirectories(destinationFile.getParent());
            if (!Files.exists(destinationFile)) {
                Files.createFile(destinationFile);
            }
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileServiceException(ExceptionMessages.File.cannotWrite(destinationFile.toString()), e);
        }

        return filename;
    }

    @Override
    public boolean fileExists(String path) {
        return new File(path).isFile();
    }

    @Override
    public Resource getFile(String path) {
        try {
            Path file = root.getRoot().resolve(path);
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() && !resource.isReadable()) {
                throw new FileServiceException(ExceptionMessages.File.unreadableFile(path));
            }

            return resource;
        } catch (MalformedURLException e) {
            throw new FileServiceException(ExceptionMessages.File.unreadableFile(path), e);
        }
    }
}
