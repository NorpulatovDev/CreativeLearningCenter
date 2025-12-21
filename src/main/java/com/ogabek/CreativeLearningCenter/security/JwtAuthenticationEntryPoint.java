package com.ogabek.CreativeLearningCenter.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.error("Unauthorized error: {}", authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String json = String.format(
                "{\"status\":%d,\"error\":\"Unauthorized\",\"message\":\"Authentication required to access this resource\",\"path\":\"%s\",\"timestamp\":\"%s\"}",
                HttpServletResponse.SC_UNAUTHORIZED,
                request.getRequestURI(),
                LocalDateTime.now()
        );

        response.getOutputStream().print(json);
    }
}
