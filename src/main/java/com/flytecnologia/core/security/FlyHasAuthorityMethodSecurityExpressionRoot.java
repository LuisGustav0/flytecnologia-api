package com.flytecnologia.core.security;

import com.flytecnologia.core.config.property.FlyAppProperty;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

public class FlyHasAuthorityMethodSecurityExpressionRoot
        extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {
    private Method method;
    private Object filterObject;
    private Object returnObject;
    private Object target;

    private FlyAppProperty flyAppProperty;

    private final String ROLE_MASTER = "ROLE_DEBUG";

    public FlyHasAuthorityMethodSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }

    public FlyHasAuthorityMethodSecurityExpressionRoot(Authentication authentication,
                                                       MethodInvocation methodInvocation) {
        super(authentication);

        this.method = methodInvocation.getMethod();
    }

    public String getAuthorityCreate() {
        if (flyAppProperty.getApp().isDebug() && !flyAppProperty.getApp().isValidatePermissions())
            return ROLE_MASTER;

        FlyRoles flyRoles = target.getClass().getAnnotation(FlyRoles.class);

        if (flyRoles != null) {
            if (!StringUtils.isEmpty(flyRoles.defaultName()))
                return "ROLE_" + flyRoles.defaultName().toUpperCase() + "_C";

            return flyRoles.create();
        }

        return "ACCESS_DENIED";
    }

    public String getAuthority(String role) {
        if (flyAppProperty.getApp().isDebug() && !flyAppProperty.getApp().isValidatePermissions())
            return ROLE_MASTER;

        return role;
    }

    public String getAuthorityRead() {
        if (flyAppProperty.getApp().isDebug() && !flyAppProperty.getApp().isValidatePermissions())
            return ROLE_MASTER;

        FlyRoles flyRoles = target.getClass().getAnnotation(FlyRoles.class);

        if (flyRoles != null) {
            if (!StringUtils.isEmpty(flyRoles.defaultName()))
                return "ROLE_" + flyRoles.defaultName().toUpperCase() + "_R";

            return flyRoles.read();
        }

        return "ACCESS_DENIED";
    }

    public String getAuthorityUpdate() {
        if (flyAppProperty.getApp().isDebug() && !flyAppProperty.getApp().isValidatePermissions())
            return ROLE_MASTER;

        FlyRoles flyRoles = target.getClass().getAnnotation(FlyRoles.class);

        if (flyRoles != null) {
            if (!StringUtils.isEmpty(flyRoles.defaultName()))
                return "ROLE_" + flyRoles.defaultName().toUpperCase() + "_U";
            return flyRoles.update();
        }

        return "ACCESS_DENIED";
    }

    public String getAuthorityDelete() {
        if (flyAppProperty.getApp().isDebug() && !flyAppProperty.getApp().isValidatePermissions())
            return ROLE_MASTER;

        FlyRoles flyRoles = target.getClass().getAnnotation(FlyRoles.class);

        if (flyRoles != null) {
            if (!StringUtils.isEmpty(flyRoles.defaultName()))
                return "ROLE_" + flyRoles.defaultName().toUpperCase() + "_D";
            return flyRoles.delete();
        }

        return "ACCESS_DENIED";
    }

    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    public Object getFilterObject() {
        return filterObject;
    }

    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    public Object getReturnObject() {
        return returnObject;
    }

    @Override
    public Object getThis() {
        return target;
    }

    public void setThis(Object target) {
        this.target = target;
    }

    public void setFlyAppProperty(FlyAppProperty flyAppProperty) {
        this.flyAppProperty = flyAppProperty;
    }
}