package com.tp.opencourse.repository;

import com.tp.opencourse.entity.ContentProcess;

import java.util.Optional;

public interface ContentProcessRepository {
    Optional<ContentProcess> findById(String id);

    ContentProcess save(ContentProcess contentProcess);
}
