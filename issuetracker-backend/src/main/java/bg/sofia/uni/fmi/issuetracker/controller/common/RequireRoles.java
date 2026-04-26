package bg.sofia.uni.fmi.issuetracker.controller.common;

import bg.sofia.uni.fmi.issuetracker.model.auth.Role;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRoles {
    Role[] roles();

    boolean strict() default false;
}
