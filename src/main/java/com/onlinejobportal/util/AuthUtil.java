package com.onlinejobportal.util;

import com.onlinejobportal.security.UserPrincipal;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public final class AuthUtil {

    private AuthUtil() {
        // Utility class - prevent instantiation
    }

    /**
     * Get the currently authenticated user's ID.
     *
     * @return Optional containing the user ID if authenticated, empty otherwise
     */
    public static Optional<Long> getCurrentUserId() {
        return getCurrentUser().map(UserPrincipal::getId);
    }

    /**
     * Get the currently authenticated user's email.
     *
     * @return Optional containing the email if authenticated, empty otherwise
     */
    public static Optional<String> getCurrentUserEmail() {
        return getCurrentUser().map(UserPrincipal::getEmail);
    }

    /**
     * Get the currently authenticated user's role.
     *
     * @return Optional containing the role name if authenticated, empty otherwise
     */
    public static Optional<String> getCurrentUserRole() {
        return getCurrentUser().map(UserPrincipal::getRole);
    }

    /**
     * Get the currently authenticated user's full name.
     *
     * @return Optional containing the full name if authenticated, empty otherwise
     */
    public static Optional<String> getCurrentUserFullName() {
        return getCurrentUser().map(UserPrincipal::getFullName);
    }

    /**
     * Get the full UserPrincipal of the currently authenticated user.
     *
     * @return Optional containing UserPrincipal if authenticated, empty otherwise
     */
    public static Optional<UserPrincipal> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return Optional.of(userPrincipal);
        }

        return Optional.empty();
    }

    /**
     * Check if the current user has a specific role.
     *
     * @param roleName the role name to check (e.g., "ADMIN", "EMPLOYER", "JOB_SEEKER")
     * @return true if the current user has the specified role
     */
    public static boolean hasRole(String roleName) {
        return getCurrentUserRole()
                .map(role -> role.equalsIgnoreCase(roleName))
                .orElse(false);
    }

    /**
     * Check if the current user is authenticated.
     *
     * @return true if a user is currently authenticated
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken);
    }

    /**
     * Get current user ID, throwing an exception if not authenticated.
     * Use this for endpoints that require authentication.
     *
     * @return the current user's ID
     * @throws IllegalStateException if no authenticated user is found
     */
    public static Long getCurrentUserIdOrThrow() {
        return getCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("No authenticated user found"));
    }

}
