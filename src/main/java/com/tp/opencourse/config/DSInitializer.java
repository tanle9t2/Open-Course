package com.tp.opencourse.config;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class DSInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    private static final String LOCATION = "/temp";
    private static final long MAX_FILE_SIZE = 5242880; // 5MB
    private static final long MAX_REQUEST_SIZE = 20971520; // 20MB
    private static final int FILE_SIZE_THRESHOLD = 1048576; // 1MB

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[0];
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{AppConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
                LOCATION, MAX_FILE_SIZE, MAX_REQUEST_SIZE, FILE_SIZE_THRESHOLD
        );
        registration.setMultipartConfig(multipartConfigElement);
    }
}
