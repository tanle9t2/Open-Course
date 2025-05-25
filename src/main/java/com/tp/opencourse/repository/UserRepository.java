package com.tp.opencourse.repository;

import com.tp.opencourse.dto.Page;
import com.tp.opencourse.entity.User;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    void save(User user);

    Long count(String kw);

    Page<User> findAll(String keyword, int page, int size, String sortBy, String direction);

    Optional<User> findById(String id);

    Optional<User> findByUsername(String id);

    Page<User> findAllUserByRole(String role, String kw, int page, int size);

    Long countTeacher();

    Optional<User> findByUsernameOrEmail(String usernameOrEmail);

    Optional<User> findByEmail(String email);

    List<User> findAllUserInCourse(String courseId);

    boolean existsByEmail(String email);
}