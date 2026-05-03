package bg.sofia.uni.fmi.issuetracker.controller;

import bg.sofia.uni.fmi.issuetracker.dto.input.auth.UserRegisterDTO;
import bg.sofia.uni.fmi.issuetracker.service.contract.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AuthController.class})
@AutoConfigureMockMvc
public class AuthControllerTests extends BaseControllerTests {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    public void testReturnsBadRequestOnInvalidData() throws Exception {
        UserRegisterDTO invalid = new UserRegisterDTO("", "Last", "User", "email@email.com", "company", "Pass");
        String invalidUserJSON = OBJECT_MAPPER.writeValueAsString(invalid);

        mockMvc.perform(post("/auth/register").contentType("application/json").content(invalidUserJSON))
                .andExpect(status().isBadRequest());
    }
}
