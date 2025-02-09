package com.example.const_team1_backend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        String errorMessage = (String) request.getAttribute("jwt_error");
        if (errorMessage == null) {
            errorMessage = "필터 에러";
        }
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, errorMessage);
    }
}
