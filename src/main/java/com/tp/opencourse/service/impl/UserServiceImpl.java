package com.tp.opencourse.service.impl;

import com.tp.opencourse.dto.response.UserProfileResponse;
import com.tp.opencourse.entity.User;

import com.tp.opencourse.exceptions.BadRequestException;
import com.tp.opencourse.exceptions.ResourceNotFoundExeption;
import com.tp.opencourse.mapper.UserMapper;
import com.tp.opencourse.repository.UserRepository;
import com.tp.opencourse.service.UserService;
import com.tp.opencourse.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;


@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserProfileResponse getProfile() {
        Authentication authentication = SecurityUtils.getAuthentication();
        User user = userRepository.findByUsername(((UserDetails) authentication.getPrincipal()).getUsername())
                .orElseThrow(() -> new BadRequestException("User doesn't exist"));
        return userMapper.userToUserProfileResponse(user);
    }
}