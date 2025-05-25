package com.tp.opencourse.service.impl;

import com.tp.opencourse.dto.Page;
import com.tp.opencourse.dto.SubmitionDTO;
import com.tp.opencourse.dto.Page;
import com.tp.opencourse.dto.UserAuthDTO;
import com.tp.opencourse.dto.response.PageResponseT;
import com.tp.opencourse.dto.response.TeacherRevenueResponse;
import com.tp.opencourse.dto.request.UserAdminRequest;
import com.tp.opencourse.dto.response.PageResponse;
import com.tp.opencourse.dto.response.UserAdminResponse;
import com.tp.opencourse.dto.response.UserProfileResponse;
import com.tp.opencourse.entity.Role;
import com.tp.opencourse.entity.User;

import com.tp.opencourse.exceptions.BadRequestException;
import com.tp.opencourse.exceptions.ResourceNotFoundExeption;
import com.tp.opencourse.mapper.UserMapper;
import com.tp.opencourse.repository.RoleRepository;
import com.tp.opencourse.repository.UserRepository;
import com.tp.opencourse.service.CloudinaryService;
import com.tp.opencourse.service.UserService;
import com.tp.opencourse.utils.Helper;
import com.tp.opencourse.utils.SecurityUtils;
import com.tp.opencourse.utils.ValidationUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.Optional;


@RequiredArgsConstructor
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder encoder;

    @Override
    public UserProfileResponse getProfile() {
        Authentication authentication = SecurityUtils.getAuthentication();
        User user = userRepository.findByUsername(((UserDetails) authentication.getPrincipal()).getUsername())
                .orElseThrow(() -> new BadRequestException("User doesn't exist"));
        return userMapper.userToUserProfileResponse(user);
    }


    @Override
    public PageResponseT<TeacherRevenueResponse> getAllProfileTeacher(String kw, int page, int size) {
        Page<User> user = userRepository.findAllUserByRole("TEACHER", kw, page, size);
        return PageResponseT.<TeacherRevenueResponse>builder()
                .totalPages(user.getTotalPages())
                .status(HttpStatus.OK)
                .data(user.getContent().stream()
                        .map(s -> userMapper.convertTeacherRevenueResponse(s))
                        .collect(Collectors.toList()))
                .page(user.getPageNumber())
                .count((long) user.getContent().size())
                .build();
    }

    @Override
    public List<UserAuthDTO> findAllStudentInCourse(String courseId) {
        List<UserAuthDTO> userAuthDTOS = userRepository.findAllUserInCourse(courseId)
                .stream()
                .map(s -> userMapper.userToUserAuthDTO(s))
                .collect(Collectors.toList());
        return userAuthDTOS;
    }


    @Override
    public PageResponse<UserAdminResponse> findAll(String keyword, Integer page, Integer size, String sortBy, String direction) {

        page = page != null ? page : 1;
        size = size != null ? size : 3;

        Page<User> users = userRepository.findAll(keyword, page, size, sortBy, direction);

        return PageResponse
                .<UserAdminResponse>builder()
                .content(users
                        .getContent()
                        .stream()
                        .map(userMapper::userToUserAdminResponse)
                        .collect(Collectors.toList()))
                .page(users.getPageNumber())
                .size(users.getPageSize())
                .totalPages(users.getTotalPages())
                .totalElements(users.getTotalElements())
                .build();
    }


    @Override
    public UserAdminResponse findById(String id) {
        User user = userRepository
                .findById(id).orElseThrow(() -> new ResourceNotFoundExeption("User with id doesn't exist"));
        return userMapper.userToUserAdminResponse(user);
    }

    @Override
    public User updateProfile(Map<String, String> fields, MultipartFile file) {
        Authentication authentication = SecurityUtils.getAuthentication();
        User user = userRepository.findByUsername(((UserDetails) authentication.getPrincipal()).getUsername())
                .orElseThrow(() -> new BadRequestException("User doesn't exist"));

        String firstName = fields.getOrDefault("firstName", "");
        String lastName = fields.getOrDefault("lastName", "");
        String phoneNumber = fields.getOrDefault("phoneNumber", "");
        String sex = fields.getOrDefault("sex", "");
        String dob = fields.getOrDefault("dob", "");
        String image = fields.getOrDefault("image", "");

        if (firstName.isBlank() || lastName.isBlank() || sex.isBlank()) {
            throw new BadRequestException("First name, last name, sex can't be blank");
        }
        if (!ValidationUtils.isNullOrEmpty(phoneNumber) && (phoneNumber.length() > 10 || !phoneNumber.matches("\\d+"))) {
            throw new BadRequestException("Invalid phone number");
        }
        if (!sex.equals("male") && !sex.equals("female")) {
            throw new BadRequestException("Invalid sex");
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber(phoneNumber);
        user.setSex(sex.equals("male"));

        if (!dob.isBlank()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate dateOfBirth = LocalDate.parse(dob, formatter);
                user.setDob(dateOfBirth);
            } catch (Exception e) {
                throw new BadRequestException("Invalid date of birth");
            }
        }

        try {
            if (!image.equals(user.getAvt())) {
                if(user.getAvt() != null) {
                    cloudinaryService.removeResource(user.getAvt(), "image");
                    user.setAvt(null);
                }
                if(file != null) {
                    String url = cloudinaryService.uploadImage(file);
                    user.setAvt(url);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        userRepository.save(user);
        return user;
    }

    @Override
    public void updateUser(UserAdminRequest userRequest, List<String> roleNames, MultipartFile multipartFile) throws IOException {
        validateForUpdate(userRequest);

        User user = userRepository.findById(userRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundExeption("User not found "));

        if (roleNames == null) {
            throw new BadRequestException("Role can't be null");
        } else if (!roleNames.contains("STUDENT")) {
            throw new BadRequestException("Must have student role");
        }


        List<Role> roles = roleNames.stream().map(roleRepository::findByName)
                .collect(Collectors.toCollection(ArrayList::new));
        user.setRoles(roles);
        user.setSex(userRequest.getSex());
        user.setUsername(userRequest.getUsername());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setActive(userRequest.getActive());
        if (!ValidationUtils.isNullOrEmpty(userRequest.getPassword())) {
            user.setPassword(encoder.encode(userRequest.getPassword()));
        }


        try {
            if (!multipartFile.isEmpty()) {
                if (user.getAvt() != null)
                    cloudinaryService.removeResource(userRequest.getAvt(), "image");
                String url = cloudinaryService.uploadImage(multipartFile);
                user.setAvt(url);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (userRequest.getDob() != null) {
            user.setDob(userRequest.getDob());
        }

        userRepository.save(user);
    }

    public void validateForUpdate(UserAdminRequest userRequest) {

        if (ValidationUtils.isNullOrEmpty(userRequest.getUsername()) ||
                ValidationUtils.isNullOrEmpty(userRequest.getEmail()) ||
                ValidationUtils.isNullOrEmpty(userRequest.getFirstName()) ||
                ValidationUtils.isNullOrEmpty(userRequest.getLastName())) {
            throw new BadRequestException("First name, last name, username, email can't be blank");
        }

        if (!ValidationUtils.isNullOrEmpty(userRequest.getPhoneNumber()) &&
                (userRequest.getPhoneNumber().length() > 10 || !userRequest.getPhoneNumber().matches("\\d+"))) {
            throw new BadRequestException("Invalid phone number");
        }

        if (!ValidationUtils.isValidEmail(userRequest.getEmail()))
            throw new BadCredentialsException("Invalid email format");


        User user = userRepository.findById(userRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundExeption("User not found "));
        Optional<User> checkUsernameUser = userRepository.findByUsername(userRequest.getUsername());
        if (checkUsernameUser.isPresent()) {
            if (!checkUsernameUser.get().getId().equals(user.getId())) {
                throw new BadRequestException("Username already exist");
            }
        }

        Optional<User> checkEmailUser = userRepository.findByEmail(userRequest.getEmail());
        if (checkEmailUser.isPresent()) {
            if (!checkEmailUser.get().getId().equals(user.getId())) {
                throw new BadRequestException("Email already exist");
            }
        }
    }
}


















