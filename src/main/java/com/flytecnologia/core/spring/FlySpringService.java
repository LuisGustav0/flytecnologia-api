package com.flytecnologia.core.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("singleton")
@Component
public class FlySpringService implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        initializeApplicationContext(applicationContext);
    }

    private static void initializeApplicationContext(ApplicationContext applicationContext) {
        FlySpringService.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return FlySpringService.applicationContext;
    }

    public static <T> T getBean(String aName) {
        if (FlySpringService.applicationContext != null) {
            return (T) FlySpringService.applicationContext.getBean(aName);
        }
        return null;
    }

    public static <T> T getBean(Class<T> aClass) {
        if (FlySpringService.applicationContext != null) {
            return FlySpringService.applicationContext.getBean(aClass);
        }
        return null;
    }
}
