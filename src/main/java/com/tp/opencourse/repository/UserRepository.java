package com.tp.opencourse.repository;

import com.tp.opencourse.entity.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(String id);
    Optional<User> findByUsername(String id);
    boolean existsByEmail(String email);
}
