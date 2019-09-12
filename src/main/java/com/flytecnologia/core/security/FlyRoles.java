package com.flytecnologia.core.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.flytecnologia.core.security.FlyPermissionService.ACCESS_DENIED;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FlyRoles {
    String defaultName() default "";
    String create() default ACCESS_DENIED;
    String read() default ACCESS_DENIED;
    String update() default ACCESS_DENIED;
    String delete() default ACCESS_DENIED;
}
