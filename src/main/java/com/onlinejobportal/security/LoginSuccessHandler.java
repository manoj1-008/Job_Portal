package com.onlinejobportal.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        log.info("User logged in successfully: email={}, role={}", userPrincipal.getEmail(), userPrincipal.getRole());

        String redirectUrl = determineRedirectUrl(userPrincipal);

        response.sendRedirect(redirectUrl);
    }

    private String determineRedirectUrl(UserPrincipal userPrincipal) {
        String role = userPrincipal.getRole().toUpperCase();
        // Database stores roles with ROLE_ prefix (ROLE_ADMIN, ROLE_EMPLOYER, ROLE_JOBSEEKER)
        if (role.contains("ADMIN")) {
            return "/admin/dashboard";
        } else if (role.contains("EMPLOYER")) {
            return "/employer/dashboard";
        } else if (role.contains("JOBSEEKER")) {
            return "/student/dashboard";
        }
        return "/";
    }

}
