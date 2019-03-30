package com.flytecnologia.core.security;

import com.flytecnologia.core.config.property.FlyAppProperty;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AuthenticationTrustResolver;

public interface FlyHasAuthorityMethodSecurityExpressionRootService extends MethodSecurityExpressionOperations {
    String getAuthorityCreate();

    String getAuthority(String role);

    String getAuthorityRead();

    String getAuthorityUpdate();

    String getAuthorityDelete();

    void setThis(Object target);

    void setFlyAppProperty(FlyAppProperty flyAppProperty);

    void setPermissionEvaluator(PermissionEvaluator permissionEvaluator);

    void setTrustResolver(AuthenticationTrustResolver trustResolver);

    void setRoleHierarchy(RoleHierarchy roleHierarchy);

    void setDefaultRolePrefix(String defaultRolePrefix);
}
