package com.flytecnologia.core.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("singleton")
@Component
public class FlySpringUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        initializeApplicationContext(applicationContext);
    }

    private static void initializeApplicationContext(ApplicationContext applicationContext) {
        FlySpringUtils.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return FlySpringUtils.applicationContext;
    }

    public static <T> T getBean(String aName) {
        if (FlySpringUtils.applicationContext != null) {
            return (T) FlySpringUtils.applicationContext.getBean(aName);
        }
        return null;
    }

    public static <T> T getBean(Class<T> aClass) {
        if (FlySpringUtils.applicationContext != null) {
            return FlySpringUtils.applicationContext.getBean(aClass);
        }
        return null;
    }
}
