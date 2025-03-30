package com.tp.opencourse.repository;

import com.tp.opencourse.entity.Comment;

import java.util.Optional;

public interface CommentRepository {

    void save(Comment comment);

    void delete(Comment comment);

    Optional<Comment> findById(String id);
}
