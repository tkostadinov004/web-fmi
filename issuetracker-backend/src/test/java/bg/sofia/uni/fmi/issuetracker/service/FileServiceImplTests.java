package bg.sofia.uni.fmi.issuetracker.service;

import bg.sofia.uni.fmi.issuetracker.utils.FileServiceRoot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;

@ExtendWith(MockitoExtension.class)
public class FileServiceImplTests {
    @Mock
    private Path rootPath;

    @Mock
    private FileServiceRoot rootMock;

    @BeforeEach
    public void setUp() {
        //when(rootPath)
    }
}
