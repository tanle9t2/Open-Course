package com.tp.opencourse.service.impl;

import com.tp.opencourse.entity.User;

import com.tp.opencourse.exceptions.ResourceNotFoundExeption;
import com.tp.opencourse.repository.UserRepository;
import com.tp.opencourse.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
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

}