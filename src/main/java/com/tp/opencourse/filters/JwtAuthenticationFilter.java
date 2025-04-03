package com.tp.opencourse.filters;

import com.tp.opencourse.entity.Token;
import com.tp.opencourse.exceptions.AccessDeniedException;
import com.tp.opencourse.exceptions.ResourceNotFoundExeption;
import com.tp.opencourse.service.TokenService;
import com.tp.opencourse.service.impl.JwtService;
import com.tp.opencourse.service.impl.UserServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserServiceImpl userService;


    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            token = jwtService.extractToken(token);
            //Validate the expired date, necessary field...
            if (jwtService.validateToken(token)) {
                String username = jwtService.extractUsername(token);
                String uuid = jwtService.extractUuid(token);
                Token redisToken = tokenService.get(uuid);

                if (redisToken == null)
                    throw new AccessDeniedException("Token not existed");

                UserDetails userDetails = userService.loadUserByUsername(username);
                if (userDetails != null) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
                filterChain.doFilter(request, response);
            } else
                throw new ResourceNotFoundExeption("Expired token");
        } catch (ResourceNotFoundExeption runtimeException) {
            filterChain.doFilter(request, response);
        }
    }

}