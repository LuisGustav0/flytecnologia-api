package com.flytecnologia.core.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class FlySpringUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        initializeApplicationContext(applicationContext);
    }

    private static void initializeApplicationContext( ApplicationContext applicationContext) {
        FlySpringUtils.applicationContext = applicationContext;
    }

    public static Object getBean(String name){
        return getApplicationContext().getBean(name);
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
