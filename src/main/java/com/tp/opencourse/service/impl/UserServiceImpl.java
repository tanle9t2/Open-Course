package com.tp.opencourse.service.impl;

import com.tp.opencourse.dto.UserAuthDTO;
import com.tp.opencourse.dto.response.UserProfileResponse;
import com.tp.opencourse.entity.User;

import com.tp.opencourse.exceptions.BadRequestException;
import com.tp.opencourse.exceptions.ResourceNotFoundExeption;
import com.tp.opencourse.mapper.UserMapper;
import com.tp.opencourse.repository.UserRepository;
import com.tp.opencourse.service.CloudinaryService;
import com.tp.opencourse.service.UserService;
import com.tp.opencourse.utils.Helper;
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
import org.springframework.web.multipart.MultipartFile;

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
    private final UserMapper userMapper;
    private final CloudinaryService cloudinaryService;

    @Override
    public UserProfileResponse getProfile() {
        Authentication authentication = SecurityUtils.getAuthentication();
        User user = userRepository.findByUsername(((UserDetails) authentication.getPrincipal()).getUsername())
                .orElseThrow(() -> new BadRequestException("User doesn't exist"));
        return userMapper.userToUserProfileResponse(user);
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
    public void updateProfile(Map<String, String> fields, MultipartFile file) {
        Authentication authentication = SecurityUtils.getAuthentication();
        User user = userRepository.findByUsername(((UserDetails) authentication.getPrincipal()).getUsername())
                .orElseThrow(() -> new BadRequestException("User doesn't exist"));

        String firstName = fields.getOrDefault("firstName", "");
        String lastName = fields.getOrDefault("lastName", "");
        String phoneNumber = fields.getOrDefault("phoneNumber", "");
        String sex = fields.getOrDefault("sex", "");
        String dob = fields.getOrDefault("dob", "");




        if(firstName.isBlank() || lastName.isBlank() || sex.isBlank() || phoneNumber.isBlank()) {
            throw new BadRequestException("First name, last name, sex can't be blank");
        }
        if(phoneNumber.length() > 10 || !phoneNumber.matches("\\d+")) {
            throw new BadRequestException("Invalid phone number");
        }
        if(!sex.equals("male") && !sex.equals("female")) {
            throw new BadRequestException("Invalid sex");
        }


        user.setFirstName(firstName);
        user.setFirstName(lastName);
        user.setLastName(lastName);
        user.setSex(sex.equals("male"));

        if(!dob.isBlank()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate dateOfBirth = LocalDate.parse(dob, formatter);
                user.setDob(dateOfBirth);
            } catch (Exception e) {
                throw new BadRequestException("Invalid date of birth");
            }
        }

        try {
            if (user.getAvt() != null && file == null) {
                cloudinaryService.removeResource(user.getAvt(), "image");
                user.setAvt(null);
            }
            if(file != null) {
                String url = cloudinaryService.uploadImage(file);
                user.setAvt(url);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        userRepository.save(user);
    }
}


















