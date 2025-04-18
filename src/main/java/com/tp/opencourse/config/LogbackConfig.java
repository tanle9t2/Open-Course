package com.tp.opencourse.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.core.ConsoleAppender;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
@Configuration
public class LogbackConfig {
    @PostConstruct
    public void configureLogging() {
        // Get the LoggerContext (the root logging context for Logback)
        ch.qos.logback.classic.LoggerContext context = (ch.qos.logback.classic.LoggerContext) LoggerFactory.getILoggerFactory();

        // Create a PatternLayout that determines how the logs will appear
        PatternLayout layout = new PatternLayout();
        layout.setPattern("%d{yyyy-MM-dd HH:mm:ss} - %msg%n");
        layout.setContext(context);
        layout.start();

        // Create a ConsoleAppender to log to the console
        ConsoleAppender appender = new ConsoleAppender();
        appender.setLayout(layout);
        appender.setContext(context);
        appender.start();

        // Get the root logger and set the log level
        Logger rootLogger = context.getLogger("ROOT");
        rootLogger.setLevel(Level.INFO); // Set the log level to INFO
        rootLogger.addAppender(appender); // Add the appender to the root logger

        context.getLogger("org.apache.kafka.clients.NetworkClient").setLevel(Level.WARN);
        context.getLogger("org.apache.kafka.clients.consumer.internals").setLevel(Level.WARN);
        context.getLogger("org.springframework.kafka").setLevel(Level.WARN);
        // Optionally, you can configure a specific logger for a class
    }
}
