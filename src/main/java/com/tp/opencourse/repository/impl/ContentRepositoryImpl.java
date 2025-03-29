package com.tp.opencourse.repository.impl;

import com.tp.opencourse.entity.Content;
import com.tp.opencourse.entity.Section;
import com.tp.opencourse.mapper.ContentMapper;
import com.tp.opencourse.repository.ContentRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ContentRepositoryImpl implements ContentRepository {
    @Autowired
    private LocalSessionFactoryBean factory;


    @Override
    public Optional<Content> findContentById(String uuid) {
        Session session = factory.getObject().getCurrentSession();
        Content content = session.get(Content.class, uuid);
        return content != null ? Optional.of(content) : Optional.empty();
    }

    @Override
    public void remove(Content content) {
        Session session = factory.getObject().getCurrentSession();
        session.remove(content);
    }
}
