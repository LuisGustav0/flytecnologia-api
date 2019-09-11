package com.flytecnologia.core.security;

import com.flytecnologia.core.config.property.FlyAppProperty;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

public class FlyMethodSecurityExpressionRoot extends OAuth2MethodSecurityExpressionHandler {
    private AuthenticationTrustResolver trustResolver =
            new AuthenticationTrustResolverImpl();

    private FlyAppProperty flyAppProperty;

    public FlyMethodSecurityExpressionRoot() {
        super();
    }

    public FlyMethodSecurityExpressionRoot(FlyAppProperty flyAppProperty) {
        super();
        this.flyAppProperty = flyAppProperty;
    }

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
            Authentication authentication, MethodInvocation invocation) {
        final FlyHasAuthorityMethodSecurityExpressionRootService root =
                new FlyHasAuthorityMethodSecurityExpressionRootServiceImpl(authentication, invocation);

        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(this.trustResolver);
        root.setRoleHierarchy(getRoleHierarchy());
        root.setThis(invocation.getThis());
        root.setDefaultRolePrefix(getDefaultRolePrefix());
        root.setFlyAppProperty(flyAppProperty);
        return root;
    }

    @Override
    protected String getDefaultRolePrefix() {
        return "";
    }

    public AuthenticationTrustResolver getTrustResolver() {
        return trustResolver;
    }

    public void setTrustResolver(AuthenticationTrustResolver trustResolver) {
        this.trustResolver = trustResolver;
    }
}