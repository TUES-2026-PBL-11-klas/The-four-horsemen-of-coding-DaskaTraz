package com.example.ElDnevniko.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.example.ElDnevniko.domain.dtos.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
@Component
public class AuthEntryPointJwt  implements AuthenticationEntryPoint {
    private ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException 
    {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        ApiResponse<String> apiResponse = ApiResponse.error(
            authException.getMessage() != null ? 
            authException.getMessage() : 
            "Unauthorized access - JWT token missing or invalid"
        );
        
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}