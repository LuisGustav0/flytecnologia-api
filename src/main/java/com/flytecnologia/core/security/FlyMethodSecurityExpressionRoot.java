package com.flytecnologia.core.security;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

public class FlyMethodSecurityExpressionRoot extends OAuth2MethodSecurityExpressionHandler {
    private AuthenticationTrustResolver trustResolver =
            new AuthenticationTrustResolverImpl();

    public FlyMethodSecurityExpressionRoot() {
        super();
    }

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
            Authentication authentication, MethodInvocation invocation) {
        FlyHasAuthorityMethodSecurityExpressionRoot root =
                new FlyHasAuthorityMethodSecurityExpressionRoot(authentication, invocation);

        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(this.trustResolver);
        root.setRoleHierarchy(getRoleHierarchy());
        root.setThis(invocation.getThis());
        root.setDefaultRolePrefix(getDefaultRolePrefix());

        return root;
    }

    public AuthenticationTrustResolver getTrustResolver() {
        return trustResolver;
    }

    public void setTrustResolver(AuthenticationTrustResolver trustResolver) {
        this.trustResolver = trustResolver;
    }
}