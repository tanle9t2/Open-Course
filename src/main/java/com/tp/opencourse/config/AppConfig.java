package com.tp.opencourse.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "com.tp.opencourse")
public class AppConfig implements WebMvcConfigurer {
    @Bean
    public Dotenv dotenv() {
        return Dotenv.configure()
                .directory("D:\\code\\PTHTW\\OpenCourse") // Set the correct directory
                .filename(".env")  // Ensure the filename is correct
                .ignoreIfMissing()  // Avoid crashing if the file is missing
                .load();
    }
    @Bean
    public StandardServletMultipartResolver multipartResolver() {
      return new StandardServletMultipartResolver();
    }
}
