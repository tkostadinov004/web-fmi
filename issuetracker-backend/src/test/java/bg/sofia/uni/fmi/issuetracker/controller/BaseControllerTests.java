package bg.sofia.uni.fmi.issuetracker.controller;

import bg.sofia.uni.fmi.issuetracker.filter.JwtAuthenticationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

public abstract class BaseControllerTests {
    @MockitoBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

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
}
