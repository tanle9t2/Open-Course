package com.tp.opencourse.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tp.opencourse.design_pattern.login.PermissionStrategyFactory;
import com.tp.opencourse.dto.TokenDTO;
import com.tp.opencourse.dto.UserAuthDTO;
import com.tp.opencourse.dto.request.*;
import com.tp.opencourse.entity.Role;
import com.tp.opencourse.entity.Token;
import com.tp.opencourse.entity.User;
import com.tp.opencourse.entity.enums.UserType;
import com.tp.opencourse.exceptions.AccessDeniedException;
import com.tp.opencourse.mapper.UserMapper;
import com.tp.opencourse.repository.RegisterRepository;
import com.tp.opencourse.repository.RoleRepository;
import com.tp.opencourse.repository.TokenRedisRepository;
import com.tp.opencourse.repository.UserRepository;
import com.tp.opencourse.service.AuthService;
import com.tp.opencourse.service.UserService;
import com.tp.opencourse.utils.SecurityUtils;
import com.tp.opencourse.utils.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final TokenRedisRepository tokenRedisRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final JwtService jwtService;

    @Value(value = "${app.token.refreshTime}")
    private int refreshTime;

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public UserAuthDTO login(LoginRequest loginRequest, String roleType) {
        String usernameOrEmail = loginRequest.getUsernameOrEmail();
        String password = loginRequest.getPassword();

        if (usernameOrEmail == null || password == null)
            throw new BadCredentialsException("Wrong username/email or password");

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(usernameOrEmail, password));

        Role role = roleRepository.findByName(roleType);

        if(role == null) {
            throw new ResourceNotFoundException("Invalid role type");
        }

        UserDetails userDetail = (UserDetails) authentication.getPrincipal();
        Boolean isChecked = PermissionStrategyFactory.getStrategy(roleType)
                .authorize(new ArrayList<>(userDetail.getAuthorities()));

        if(!isChecked) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }
        userDetail.getAuthorities().stream()
                .filter(authority -> authority.getAuthority().equals("STUDENT"))
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("Permission denied"));

        com.tp.opencourse.entity.User user = userRepository.findByUsernameOrEmail(userDetail.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Username/Email doesn't exist"));

        TokenDTO tokenDTO = jwtService.generateToken(userDetail.getUsername(), user.getId());
        Token redisToken = Token.builder()
                .uuid(tokenDTO.getUuid())
                .userKey(user.getId())
                .timeToLive(refreshTime)
                .build();

        tokenRedisRepository.save(redisToken);
        UserAuthDTO userAuthDTO = userMapper.userToUserAuthDTO(user);
        userAuthDTO.setTokenDTO(tokenDTO);
        return userAuthDTO;
    }

    @Override
    public Authentication mvcLogin(AdminLoginRequest adminLoginRequest) {
        String username = adminLoginRequest.getUsername();
        String password = adminLoginRequest.getPassword();

        if (username == null || password == null)
            throw new BadCredentialsException("Wrong username/email or password");

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        Role role = roleRepository.findByName("ADMIN");

        if(role == null) {
            throw new ResourceNotFoundException("Invalid role type");
        }

        UserDetails userDetail = (UserDetails) authentication.getPrincipal();
        Boolean isChecked = PermissionStrategyFactory.getStrategy("ADMIN")
                .authorize(new ArrayList<>(userDetail.getAuthorities()));
        if(!isChecked) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }
        return authentication;
    }

    @Override
    public UserAuthDTO login(OAuthLoginRequest loginRequest) {
        Optional<User> checkingUser = userRepository.findByEmail(loginRequest.getEmail());
        User user;
        if (checkingUser.isEmpty()) {
            String randomUserSalt = UUID.randomUUID().toString().substring(0, 7);
            List<Role> roles = roleRepository.findDefaultRolesForNewlyLoggedInUser();
            user = User.builder()
                    .email(loginRequest.getEmail())
                    .username(String.format("%s-%s", loginRequest.getEmail().split("@")[0], randomUserSalt))
                    .firstName(loginRequest.getName())
                    .lastName(loginRequest.getName())
                    .type(UserType.GOOGLE)
                    .password(passwordEncoder.encode(randomUserSalt))
                    .roles(roles)
                    .build();

            userRepository.save(user);
        } else {
            user = checkingUser.get();
            if (!user.getType().equals(UserType.GOOGLE)) {
                user.setType(UserType.GOOGLE);
                userRepository.save(user);
            }
        }

        TokenDTO tokenDTO = jwtService.generateToken(user.getUsername(), user.getId());
        Token redisToken = Token.builder()
                .uuid(tokenDTO.getUuid())
                .userKey(user.getId())
                .timeToLive(refreshTime)
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
        validateForUserRegister(registerRequest);
        List<Role> roles = roleRepository.findDefaultRolesForNewlyLoggedInUser();
        User user = User.builder()
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .active(true)
                .type(UserType.DEFAULT)
                .sex(true)
                .roles(roles)
                .createdAt(LocalDate.now())
                .build();
        userRepository.save(user);
    }

    @Override
    public void register(UserAdminRegister userAdminRegister) {
        validateForUserAdminRegister(userAdminRegister);
        List<Role> roles = roleRepository.findDefaultRolesForNewlyLoggedInUser();
        User user = User.builder()
                .email(userAdminRegister.getEmail())
                .username(userAdminRegister.getUsername())
                .firstName(userAdminRegister.getFirstName())
                .lastName(userAdminRegister.getLastName())
                .password(passwordEncoder.encode(userAdminRegister.getPassword()))
                .active(true)
                .type(UserType.DEFAULT)
                .sex(true)
                .roles(roles)
                .createdAt(LocalDate.now())
                .build();
        userRepository.save(user);
    }

    private void validateForUserAdminRegister(UserAdminRegister userAdminRegister) {
        String firstName = userAdminRegister.getFirstName();
        String lastName = userAdminRegister.getLastName();
        String username = userAdminRegister.getUsername();
        String email = userAdminRegister.getEmail();
        String password = userAdminRegister.getPassword();

        if (ValidationUtils.isNullOrEmpty(lastName)
                || ValidationUtils.isNullOrEmpty(firstName)
                || ValidationUtils.isNullOrEmpty(username)
                || ValidationUtils.isNullOrEmpty(password)
                || ValidationUtils.isNullOrEmpty(email))
            throw new BadCredentialsException("Fields must not be null");

        if (!ValidationUtils.isValidEmail(email))
            throw new BadCredentialsException("Invalid email format");

        boolean isEmailExisted = userRepository.existsByEmail(email);
        if (isEmailExisted)
            throw new BadCredentialsException("Email existed !");

        Optional<com.tp.opencourse.entity.User> checkingUser = userRepository.findByUsername(username);
        if (checkingUser.isPresent()) {
            throw new BadCredentialsException("Username existed !");
        }
    }

    private void validateForUserRegister(RegisterRequest registerRequest) {
        String firstName = registerRequest.getFirstName();
        String lastName = registerRequest.getLastName();
        String username = registerRequest.getUsername();
        String email = registerRequest.getEmail();
        String password = registerRequest.getPassword();
        String confirmedPassword = registerRequest.getConfirmedPassword();

        if (ValidationUtils.isNullOrEmpty(lastName)
                || ValidationUtils.isNullOrEmpty(firstName)
                || ValidationUtils.isNullOrEmpty(username)
                || ValidationUtils.isNullOrEmpty(password)
                || ValidationUtils.isNullOrEmpty(confirmedPassword)
                || ValidationUtils.isNullOrEmpty(email))
            throw new BadCredentialsException("Fields must not be null");

        if (!password.equals(confirmedPassword))
            throw new BadCredentialsException("Password doesn't match each other");

        if (!ValidationUtils.isValidEmail(email))
            throw new BadCredentialsException("Invalid email format");

        boolean isEmailExisted = userRepository.existsByEmail(email);
        if (isEmailExisted)
            throw new BadCredentialsException("Email existed !");

        Optional<com.tp.opencourse.entity.User> checkingUser = userRepository.findByUsername(username);
        if (checkingUser.isPresent()) {
            throw new BadCredentialsException("Username existed !");
        }
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

    @Override
    @Transactional
    public void deleteAllExceptCurrentToken(String userId, String uuid) {
        List<Token> tokens = tokenRedisRepository.findAllByUserKey(userId);
        Map<Boolean, List<Token>> partitionedTokens = tokens.stream()
                .collect(Collectors.partitioningBy(filterToken -> filterToken.getUuid().equals(uuid)));

        List<Token> remainingTokens = partitionedTokens.get(false);
        if (!remainingTokens.isEmpty()) {
            tokenRedisRepository.deleteAll(remainingTokens);
        }
    }

    public String extractJsonValue(JsonObject jsonObject, String arrayName, String field) {
        JsonArray jsonArray = jsonObject.getAsJsonArray(arrayName);
        if (jsonArray != null && !jsonArray.isEmpty()) {
            return jsonArray.get(0).getAsJsonObject().get(field).getAsString();
        }
        return null;
    }
}












































