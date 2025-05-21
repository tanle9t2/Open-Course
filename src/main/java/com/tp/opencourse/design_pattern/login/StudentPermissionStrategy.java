package com.tp.opencourse.design_pattern.login;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Optional;

public class StudentPermissionStrategy implements PermissionCheckingStrategy {
    @Override
    public boolean authorize(List<GrantedAuthority> authorities) {
        Optional<GrantedAuthority> grantedAuthority = authorities.stream().filter(role ->
                        role.getAuthority().equals("STUDENT"))
                .findFirst();
        return grantedAuthority.isPresent();
    }
}
