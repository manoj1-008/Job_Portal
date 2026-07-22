package com.onlinejobportal.security;

import com.onlinejobportal.entity.User;
import com.onlinejobportal.exception.ResourceNotFoundException;
import com.onlinejobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Login attempt failed for email: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        if (!user.isEnabled()) {
            log.warn("Login attempt for disabled account: {}", email);
            throw new UsernameNotFoundException("Account is disabled. Please contact administrator.");
        }

        log.info("User authenticated successfully: {} with role {}", email, user.getRole().getName());
        return new UserPrincipal(user);
    }

    @Transactional(readOnly = true)
    public UserPrincipal loadUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return new UserPrincipal(user);
    }

}

