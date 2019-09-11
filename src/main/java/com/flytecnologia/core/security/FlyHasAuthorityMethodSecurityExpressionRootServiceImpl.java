package com.flytecnologia.core.security;

import com.flytecnologia.core.config.property.FlyAppProperty;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

public class FlyHasAuthorityMethodSecurityExpressionRootServiceImpl
        extends SecurityExpressionRoot implements FlyHasAuthorityMethodSecurityExpressionRootService {
    private Method method;
    private Object filterObject;
    private Object returnObject;
    private Object target;
    private FlyAppProperty flyAppProperty;

    private final String MASTER = "DEBUG";

    public FlyHasAuthorityMethodSecurityExpressionRootServiceImpl(Authentication authentication,
                                                                  MethodInvocation methodInvocation) {
        super(authentication);
        this.method = methodInvocation.getMethod();
    }

    @Override
    public String getAuthorityCreate() {
        if (flyAppProperty.getApp().isDebug() && !flyAppProperty.getApp().isValidatePermissions())
            return MASTER;

        final FlyRoles flyRoles = target.getClass().getAnnotation(FlyRoles.class);

        if (flyRoles != null) {
            if (!StringUtils.isEmpty(flyRoles.defaultName()))
                return flyRoles.defaultName().toUpperCase() + "_C";

            return flyRoles.create();
        }

        return "ACCESS_DENIED";
    }

    @Override
    public String getAuthority(String role) {
        if (flyAppProperty.getApp().isDebug() && !flyAppProperty.getApp().isValidatePermissions())
            return MASTER;

        return role;
    }

    @Override
    public String getAuthorityRead() {
        if (flyAppProperty.getApp().isDebug() && !flyAppProperty.getApp().isValidatePermissions())
            return MASTER;

        final FlyRoles flyRoles = target.getClass().getAnnotation(FlyRoles.class);

        if (flyRoles != null) {
            if (!StringUtils.isEmpty(flyRoles.defaultName()))
                return flyRoles.defaultName().toUpperCase() + "_R";

            return flyRoles.read();
        }

        return "ACCESS_DENIED";
    }

    @Override
    public String getAuthorityUpdate() {
        if (flyAppProperty.getApp().isDebug() && !flyAppProperty.getApp().isValidatePermissions())
            return MASTER;

        final FlyRoles flyRoles = target.getClass().getAnnotation(FlyRoles.class);

        if (flyRoles != null) {
            if (!StringUtils.isEmpty(flyRoles.defaultName()))
                return flyRoles.defaultName().toUpperCase() + "_U";
            return flyRoles.update();
        }

        return "ACCESS_DENIED";
    }

    @Override
    public String getAuthorityDelete() {
        if (flyAppProperty.getApp().isDebug() && !flyAppProperty.getApp().isValidatePermissions())
            return MASTER;

        final FlyRoles flyRoles = target.getClass().getAnnotation(FlyRoles.class);

        if (flyRoles != null) {
            if (!StringUtils.isEmpty(flyRoles.defaultName()))
                return flyRoles.defaultName().toUpperCase() + "_D";
            return flyRoles.delete();
        }

        return "ACCESS_DENIED";
    }

    @Override
    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    @Override
    public Object getFilterObject() {
        return filterObject;
    }

    @Override
    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    @Override
    public Object getReturnObject() {
        return returnObject;
    }

    @Override
    public Object getThis() {
        return target;
    }

    @Override
    public void setThis(Object target) {
        this.target = target;
    }

    @Override
    public void setFlyAppProperty(FlyAppProperty flyAppProperty) {
        this.flyAppProperty = flyAppProperty;
    }
}