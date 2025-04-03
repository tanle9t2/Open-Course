package com.tp.opencourse.config;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletRegistration;
import org.springframework.security.access.SecurityConfig;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class DSInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    private static final String LOCATION = "/temp";
    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024 * 1024; // 10GB
    private static final long MAX_REQUEST_SIZE = 10L * 1024 * 1024 * 1024; // 10GB
    private static final int FILE_SIZE_THRESHOLD = 10 * 1024 * 1024 * 1024; // 10GB


    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{
                AppConfig.class,
                HibernateConfig.class,
                WebSecurityConfig.class,

        };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[0];
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
