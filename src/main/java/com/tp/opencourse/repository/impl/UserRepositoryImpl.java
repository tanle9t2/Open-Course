package com.tp.opencourse.repository.impl;

import com.tp.opencourse.entity.User;
import com.tp.opencourse.repository.UserRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserRepositoryImpl implements UserRepository {
    @Autowired
    private LocalSessionFactoryBean factoryBean;

    @Override
    public Optional<User> findById(String id) {
        Session session = factoryBean.getObject().getCurrentSession();
        User user = session.get(User.class, id);
        return user != null ? Optional.of(user) : Optional.empty();
    }
}
