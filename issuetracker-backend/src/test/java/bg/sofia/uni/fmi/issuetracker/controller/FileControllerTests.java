package bg.sofia.uni.fmi.issuetracker.controller;

import bg.sofia.uni.fmi.issuetracker.dto.input.file.FetchFileDTO;
import bg.sofia.uni.fmi.issuetracker.exception.NotFoundException;
import bg.sofia.uni.fmi.issuetracker.exception.file.FileServiceException;
import bg.sofia.uni.fmi.issuetracker.service.contract.FileService;
import bg.sofia.uni.fmi.issuetracker.utils.messages.ExceptionMessages;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {FileController.class})
public class FileControllerTests extends BaseControllerTests {
    private static final FetchFileDTO DTO = new FetchFileDTO("user1/test.txt");

    @MockitoBean
    private FileService fileService;

    @Test
    public void testGetFile_ReturnsOk() throws Exception {
        ByteArrayResource resource = new ByteArrayResource("file-content".getBytes());
        when(fileService.getFile(DTO.path())).thenReturn(resource);

        mockMvc.perform(get("/files")
                        .contentType("application/json")
                        .content(OBJECT_MAPPER.writeValueAsString(DTO)))
                .andExpect(status().isOk())
                .andExpect(content().bytes("file-content".getBytes()));
    }

    @Test
    public void testGetFile_ReturnsBadRequestOnInvalidFile() throws Exception {
        when(fileService.getFile(DTO.path())).thenThrow(new FileServiceException(ExceptionMessages.File.invalidFile()));

        mockMvc.perform(get("/files")
                        .contentType("application/json")
                        .content(OBJECT_MAPPER.writeValueAsString(DTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.File.invalidFile()));
    }

    @Test
    public void testGetFile_ReturnsNotFoundWhenMissing() throws Exception {
        when(fileService.getFile(DTO.path())).thenThrow(new NotFoundException(ExceptionMessages.File.invalidFile()));

        mockMvc.perform(get("/files")
                        .contentType("application/json")
                        .content(OBJECT_MAPPER.writeValueAsString(DTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ExceptionMessages.File.invalidFile()));
    }

    @Test
    public void testUnauthorizedRequest_ReturnsUnauthorized() throws Exception {
        super.unauthorizedRequest(get("/files/user1/test.txt"));
    }
}
