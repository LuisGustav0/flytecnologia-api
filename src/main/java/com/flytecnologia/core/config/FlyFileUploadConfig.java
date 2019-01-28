package com.flytecnologia.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.MultipartFilter;

@Configuration
public class FlyFileUploadConfig {

    @Bean
    public CommonsMultipartResolver commonsMultipartResolver() {
        final CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
        commonsMultipartResolver.setMaxUploadSize(-1);
        return commonsMultipartResolver;
    }

    @Bean
    @Order(0)
    public MultipartFilter multipartFilter() {
        final MultipartFilter multipartFilter = new MultipartFilter();
        multipartFilter.setMultipartResolverBeanName("multipartReso‌​lver");
        return multipartFilter;
    }
}
