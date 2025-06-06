package com.tp.opencourse.config;

import com.tp.opencourse.filters.JwtAuthenticationFilter;
import com.tp.opencourse.filters.CORSFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    private CORSFilter CORSFilter;
    @Autowired
    private AuthenticationEntryPoint customeAuthenticationEntryPoint;
    @Autowired
    private AccessDeniedHandler customAccessDeniedHandler;

    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session
                        -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/auth/login",
                                "/api/v1/auth/oauth/login",
                                "/api/v1/auth/register").permitAll()
                        .requestMatchers(
                                "/api/v1/checkout/**",
                                "/api/v1/ws/**",
                                "/api/v1/category/**",
                                "/api/v1/public/**",
                                "/api/v1/search/**",
                                "/api/v1/categories"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/courses/**",
                                "/api/v1/auth/register",
                                "/api/v1/auth/oauth/login"
                        ).permitAll()
                        .requestMatchers(
                                "/api/v1/checkout/**",
                                "/api/v1/ws/**",
                                "/api/v1/public/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/categories/**",
                                "/api/v1/search/**",
                                "/api/v1/courses/{courseId}",
                                "/api/v1/courses/multiple",
                                "/api/v1/courses/{courseId}/section",
                                "/api/v1/certifications/**",
                                "/api/v1/search/**",
                                "/api/v1/auth/oauth-url",
                                "/api/v1/content/{contentId}").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(CORSFilter, ChannelProcessingFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> {
                    exception.authenticationEntryPoint(customeAuthenticationEntryPoint);
                    exception.accessDeniedHandler(customAccessDeniedHandler);
                });

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webSecurity(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login",
                                "/register",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/vendors/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        // 👇 Redirect when not authenticated
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendRedirect("/OpenCourse/login")
                        )
                        // 👇 Redirect when authenticated but access is denied
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendRedirect("/OpenCourse/login")
                        )
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                        .permitAll()
                );

        return http.build();
    }

    @Bean(name = "mvcHandlerMappingIntrospector")
    public HandlerMappingIntrospector handlerMappingIntrospector() {
        return new HandlerMappingIntrospector();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setPasswordEncoder(passwordEncoder);
        auth.setUserDetailsService(userDetailsService);
        return auth;
    }

}
