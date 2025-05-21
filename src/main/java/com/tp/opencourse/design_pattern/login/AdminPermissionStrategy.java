package com.tp.opencourse.design_pattern.login;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Optional;

public class AdminPermissionStrategy implements PermissionCheckingStrategy {
    @Override
    public boolean authorize(List<GrantedAuthority> authorities) {
        Optional<GrantedAuthority> grantedAuthority = authorities.stream().filter(role ->
                        role.getAuthority().equals("ADMIN"))
                .findFirst();
        return grantedAuthority.isPresent();    }
}
