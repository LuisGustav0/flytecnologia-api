package com.flytecnologia.core.config;

import com.flytecnologia.core.hibernate.multitenancy.FlyMultiTenantInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FlyWebMvcConfigurer implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new FlyMultiTenantInterceptor()).addPathPatterns("/**");
    }
}
