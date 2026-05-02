package bg.sofia.uni.fmi.issuetracker.controller;

import bg.sofia.uni.fmi.issuetracker.service.contract.FileService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
public class TestFileUploadController {
    private final FileService fileService;

    public TestFileUploadController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(value = "/")
    public ResponseEntity<Resource> upload(@RequestParam("file") MultipartFile file) {
        fileService.saveFile(file, "user1/pfp.png");
        return ResponseEntity.ok(fileService.getFile("user1/pfp.png"));
    }
}
