package com.tp.opencourse.repository;

import com.tp.opencourse.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    void save(User user);

    Optional<User> findById(String id);

    Optional<User> findByUsername(String id);

    Optional<User> findByUsernameOrEmail(String usernameOrEmail);

    Optional<User> findByEmail(String email);

    List<User> findAllUserInCourse(String courseId);

    boolean existsByEmail(String email);
}