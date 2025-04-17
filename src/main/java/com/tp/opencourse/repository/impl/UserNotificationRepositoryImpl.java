package com.tp.opencourse.repository.impl;

import com.tp.opencourse.entity.UserNotification;
import com.tp.opencourse.repository.UserNotificationRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserNotificationRepositoryImpl implements UserNotificationRepository {
    @Autowired
    private LocalSessionFactoryBean factoryBean;

    @Override
    public UserNotification create(UserNotification userNotification) {
        Session session = factoryBean.getObject().getCurrentSession();
        return session.merge(userNotification);
    }

    @Override
    public List<UserNotification> findByUserId(String userId) {
        return null;
    }
}
