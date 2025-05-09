package com.tp.opencourse.repository.impl;

import com.tp.opencourse.entity.Course;
import com.tp.opencourse.entity.Notification;
import com.tp.opencourse.repository.NotificationRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class NotificationRepositoryImpl implements NotificationRepository {
    @Autowired
    private LocalSessionFactoryBean factoryBean;

    @Override
    public Notification save(Notification notification) {
        Session session = factoryBean.getObject().getCurrentSession();
        return session.merge(notification);
    }

    @Override
    public Optional<Notification> findById(String id) {
        Session session = factoryBean.getObject().getCurrentSession();
        Notification notification = session.get(Notification.class, id);

        return notification != null ? Optional.of(notification) : Optional.empty();
    }
}
