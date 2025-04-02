package com.tp.opencourse.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotDevConfig {
    @Bean
    public Dotenv dotenv() {
        return Dotenv.configure()
                .directory("/home/phat/Documents/workspace/java-project/Open-Course")
                .filename(".env")
                .ignoreIfMissing()
                .load();
    }

}
