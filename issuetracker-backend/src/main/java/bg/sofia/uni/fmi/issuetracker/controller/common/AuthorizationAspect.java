package bg.sofia.uni.fmi.issuetracker.controller.common;

import bg.sofia.uni.fmi.issuetracker.service.contract.ProjectService;
import bg.sofia.uni.fmi.issuetracker.utils.messages.OutputMessages;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

@Aspect
@Component
public class AuthorizationAspect {
    private final ProjectService projectService;

    public AuthorizationAspect(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Before("@annotation(bg.sofia.uni.fmi.issuetracker.controller.common.RequireRoles)")
    public void authorize(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        RequireRoles roleRequirements = method.getAnnotation(RequireRoles.class);

        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String projectId = getProjectId();
        boolean hasRoles = projectService.hasRoles(username,
                projectId,
                Arrays.stream(roleRequirements.roles()).toList(),
                roleRequirements.strict());

        if (!hasRoles) {
            throw new AuthorizationDeniedException(OutputMessages.System.ACCESS_DENIED);
        }
    }

    private String getProjectId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        return pathVariables.get("projectId");
    }
}
