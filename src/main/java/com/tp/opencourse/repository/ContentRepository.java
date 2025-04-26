package com.tp.opencourse.repository;

import com.tp.opencourse.entity.Content;
import com.tp.opencourse.entity.ContentProcess;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface ContentRepository {
    Optional<Content> findContentById(String uuid);

    void remove(Content content);

    Content updateContent(Content content);

    List<ContentProcess> countContentComplete(String sectionId, String userId);
}
