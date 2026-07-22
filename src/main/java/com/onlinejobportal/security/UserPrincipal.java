package com.onlinejobportal.security;

import com.onlinejobportal.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String fullName;
    private final String email;
    private final String password;
    private final String role;
    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(User user) {
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.role = user.getRole().getName();
        this.enabled = user.isEnabled();
// Role from DB already includes ROLE_ prefix (e.g. ROLE_JOBSEEKER, ROLE_EMPLOYER)
        String roleName = user.getRole().getName();
        String authority = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;
        this.authorities = Collections.singletonList(
                new SimpleGrantedAuthority(authority)
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public boolean isAdmin() {
    return "ROLE_ADMIN".equalsIgnoreCase(role);
}

public boolean isEmployer() {
    return "ROLE_EMPLOYER".equalsIgnoreCase(role);
}

public boolean isJobSeeker() {
    return "ROLE_JOBSEEKER".equalsIgnoreCase(role);
}

}

