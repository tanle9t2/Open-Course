package com.tp.opencourse.design_pattern.login;

import org.checkerframework.checker.index.qual.EnsuresLTLengthOfIf;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public interface PermissionCheckingStrategy {
    boolean authorize(List<GrantedAuthority> authority);
}
