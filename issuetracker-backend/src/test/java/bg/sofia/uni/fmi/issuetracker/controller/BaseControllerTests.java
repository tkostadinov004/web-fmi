package bg.sofia.uni.fmi.issuetracker.controller;

import bg.sofia.uni.fmi.issuetracker.filter.JwtAuthenticationFilter;
import bg.sofia.uni.fmi.issuetracker.utils.messages.OutputMessages;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tools.jackson.databind.ObjectMapper;

import static bg.sofia.uni.fmi.issuetracker.utils.CommonUtils.buildErrorResponseAsJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public abstract class BaseControllerTests {
    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @MockitoBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    protected MockMvc mockMvc;

    @BeforeEach
    protected void setUp() throws Exception {
        doAnswer(answer -> {
            ServletRequest request = answer.getArgument(0);
            ServletResponse response = answer.getArgument(1);
            FilterChain filterChain = answer.getArgument(2);
            filterChain.doFilter(request, response);
            return answer;
        }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());
    }

    protected void unauthorizedRequest(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        doAnswer(answer -> {
            HttpServletResponse response = answer.getArgument(1);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(buildErrorResponseAsJson(OutputMessages.System.UNAUTHORIZED));
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());

        mockMvc.perform(requestBuilder)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value(OutputMessages.System.UNAUTHORIZED));
    }
}
