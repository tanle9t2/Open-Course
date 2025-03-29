package com.tp.opencourse.repository;

import com.tp.opencourse.entity.Content;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface ContentRepository {
    Optional<Content> findContentById(String uuid);
    void remove(Content content);
}
