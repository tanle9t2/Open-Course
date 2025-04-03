package com.tp.opencourse.repository.impl;

import com.tp.opencourse.entity.User;
import com.tp.opencourse.repository.UserRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    @Autowired
    private LocalSessionFactoryBean factoryBean;

    @Override
    public Optional<User> findById(String id) {
        Session session = factoryBean.getObject().getCurrentSession();
        User user = session.get(User.class, id);
        return user != null ? Optional.of(user) : Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        Session session = factoryBean.getObject().getCurrentSession();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);
        query.select(root).where(builder.equal(root.get("username"), username));
        User user = session.createQuery(query).uniqueResult();
        return user != null ? Optional.of(user) : Optional.empty();
    }

    @Override
    public boolean existsByEmail(String email) {
        Session session = factoryBean.getObject().getCurrentSession();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Boolean> query = builder.createQuery(Boolean.class);
        Root<User> root = query.from(User.class);

        query.select(builder.literal(true))
                .where(builder.equal(root.get("email"), email));

        Boolean exists = session.createQuery(query).setMaxResults(1).uniqueResult();

        return Boolean.TRUE.equals(exists);
    }
}