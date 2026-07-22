package com.onlinejobportal.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String email = request.getParameter("email");
        String errorMessage;

        if (exception instanceof BadCredentialsException) {
            errorMessage = "Invalid email or password. Please try again.";
            log.warn("Failed login attempt for email: {} - Invalid credentials", email);
        } else if (exception instanceof DisabledException) {
            errorMessage = "Your account has been disabled. Please contact the administrator.";
            log.warn("Failed login attempt for email: {} - Account disabled", email);
        } else {
            errorMessage = "An unexpected error occurred. Please try again later.";
            log.error("Failed login attempt for email: {} - Error: {}", email, exception.getMessage());
        }

        response.sendRedirect("/login?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
    }

}

