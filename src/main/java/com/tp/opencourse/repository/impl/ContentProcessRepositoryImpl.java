package com.tp.opencourse.repository.impl;

import com.tp.opencourse.dto.ContentProcessDTO;
import com.tp.opencourse.entity.ContentProcess;
import com.tp.opencourse.repository.ContentProcessRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ContentProcessRepositoryImpl implements ContentProcessRepository {
    @Autowired
    private LocalSessionFactoryBean factoryBean;

    @Override
    public Optional<ContentProcess> findById(String id) {
        Session session = factoryBean.getObject().getCurrentSession();
        ContentProcess contentProcess = session.get(ContentProcess.class, id);
        return contentProcess != null ? Optional.of(contentProcess) : Optional.empty();
    }

    @Override
    public ContentProcess save(ContentProcess contentProcess) {
        Session session = factoryBean.getObject().getCurrentSession();
        return session.merge(contentProcess);
    }


}
