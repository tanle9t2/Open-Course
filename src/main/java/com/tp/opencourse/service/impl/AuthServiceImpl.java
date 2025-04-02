package com.tp.opencourse.service.impl;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tp.opencourse.dto.TokenDTO;
import com.tp.opencourse.dto.UserAuthDTO;
import com.tp.opencourse.dto.request.LoginRequest;
import com.tp.opencourse.dto.request.RegisterRequest;
import com.tp.opencourse.entity.Token;
import com.tp.opencourse.entity.User;
import com.tp.opencourse.mapper.UserMapper;
import com.tp.opencourse.repository.TokenRedisRepository;
import com.tp.opencourse.repository.UserRepository;
import com.tp.opencourse.service.AuthService;
import com.tp.opencourse.utils.SecurityUtils;
import com.tp.opencourse.utils.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final TokenRedisRepository tokenRedisRepository;

    private final JwtService jwtService;

    @Value(value = "${app.token.expirationTime}")
    private int expirationTime;

    @Override
    @Transactional
    public UserAuthDTO login(LoginRequest loginRequest) {
        String usernameOrEmail = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        if(usernameOrEmail == null || password == null)
            throw new BadCredentialsException("Wrong username/email or password");

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(usernameOrEmail, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetail = (UserDetails) authentication.getPrincipal();

        com.tp.opencourse.entity.User user = userRepository.findByUsername(userDetail.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Username/Email doesn't exist"));

        TokenDTO tokenDTO = jwtService.generateToken(userDetail.getUsername(), user.getId());
        Token redisToken = Token.builder()
                .uuid(tokenDTO.getUuid())
                .userKey(user.getId())
                .timeToLive(expirationTime)
                .build();

        tokenRedisRepository.save(redisToken);
        UserAuthDTO userAuthDTO = userMapper.userToUserAuthDTO(user);
        userAuthDTO.setTokenDTO(tokenDTO);
        return userAuthDTO;
    }


    @Override
    public TokenDTO changePassword(String newPassword, String oldPassword, Boolean isLogAllOut) {
        return null;
    }

    @Override
    @Transactional
    public void register(RegisterRequest registerRequest) {
        String name = registerRequest.getName();
        String username = registerRequest.getUsername();
        String email = registerRequest.getEmail();
        String password = registerRequest.getPassword();
        String confirmedPassword = registerRequest.getConfirmedPassword();

        if(name == null || username == null || password == null || confirmedPassword == null|| email == null)
            throw new BadCredentialsException("Fields must not be null");
        if(!password.equals(confirmedPassword))
            throw new BadCredentialsException("Password doesn't match each other");
        if(!ValidationUtils.isValidEmail(email))
            throw new BadCredentialsException("Invalid email format");

        boolean isEmailExisted = userRepository.existsByEmail(email);
        if(isEmailExisted)
            throw new BadCredentialsException("Email existed !");

        Optional<com.tp.opencourse.entity.User> checkingUser = userRepository.findByUsername(username);
        if(checkingUser.isPresent()) {
            throw new BadCredentialsException("Username existed !");
        }

//        User user = User.builder()
//                .name(name)
//                .username(username)
//                .password(passwordEncoder.encode(password))
//                .status(new User.UserStatus())
//                .build();
//        userRepository.save(user);
    }

    @Override
    @Transactional
    public void logout() {
        Authentication authentication = SecurityUtils.getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User authenticatedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid credential"));

        log.info("logout: {}", authenticatedUser.getId());
        List<Token> tokens = tokenRedisRepository.findAllByUserKey(authenticatedUser.getId());
        tokenRedisRepository.deleteAllById(tokens.stream().map(Token::getUuid).toList());
    }

    public String extractJsonValue(JsonObject jsonObject, String arrayName, String field) {
        JsonArray jsonArray = jsonObject.getAsJsonArray(arrayName);
        if(jsonArray != null && !jsonArray.isEmpty()) {
            return jsonArray.get(0).getAsJsonObject().get(field).getAsString();
        }
        return null;
    }
}










































