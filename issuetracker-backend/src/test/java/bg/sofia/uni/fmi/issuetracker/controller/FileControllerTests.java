package bg.sofia.uni.fmi.issuetracker.controller;

import bg.sofia.uni.fmi.issuetracker.exception.NotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.file.FileServiceException;
import bg.sofia.uni.fmi.issuetracker.service.contract.FileService;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {FileController.class})
public class FileControllerTests extends BaseControllerTests {
    private static final String PATH = URLEncoder.encode("user1/test.txt", StandardCharsets.UTF_8);

    @MockitoBean
    private FileService fileService;

    @Test
    public void testGetFile_ReturnsOk() throws Exception {
        ByteArrayResource resource = new ByteArrayResource("file-content".getBytes());
        when(fileService.getFile(PATH)).thenReturn(resource);

        mockMvc.perform(get("/files")
                        .param("path", PATH)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().bytes("file-content".getBytes()));
    }

    @Test
    public void testGetFile_ReturnsBadRequestOnInvalidFile() throws Exception {
        when(fileService.getFile(PATH)).thenThrow(new FileServiceException(ExceptionMessages.File.invalidFile()));

        mockMvc.perform(get("/files")
                        .param("path", PATH)
                        .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.File.invalidFile()));
    }

    @Test
    public void testGetFile_ReturnsNotFoundWhenMissing() throws Exception {
        when(fileService.getFile(PATH)).thenThrow(new NotFoundException(ExceptionMessages.File.invalidFile()));

        mockMvc.perform(get("/files")
                        .param("path", PATH)
                        .contentType("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.File.invalidFile()));
    }

    @Test
    public void testUnauthorizedRequest_ReturnsUnauthorized() throws Exception {
        super.unauthorizedRequest(get("/files/user1/test.txt"));
    }
}
