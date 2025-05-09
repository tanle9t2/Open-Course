package com.tp.opencourse.repository.impl;

import com.tp.opencourse.entity.Register;
import com.tp.opencourse.entity.User;
import com.tp.opencourse.entity.UserNotification;
import com.tp.opencourse.repository.UserNotificationRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserNotificationRepositoryImpl implements UserNotificationRepository {
    @Autowired
    private LocalSessionFactoryBean factoryBean;

    @Override
    public UserNotification save(UserNotification userNotification) {
        Session session = factoryBean.getObject().getCurrentSession();
        return session.merge(userNotification);
    }

    @Override
    public List<UserNotification> findByUserId(String username) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<UserNotification> query = builder.createQuery(UserNotification.class);

        Root<UserNotification> root = query.from(UserNotification.class);
        Join<UserNotification, User> userJoin = root.join("student");
        query.select(root)
                .where(builder.equal(userJoin.get("username"), username));
        return session.createQuery(query).getResultList();
    }

    @Override
    public Optional<UserNotification> findById(String id) {
        Session session = factoryBean.getObject().getCurrentSession();
        UserNotification userNotification = session.get(UserNotification.class, id);
        return userNotification != null ? Optional.of(userNotification) : Optional.empty();
    }
}
