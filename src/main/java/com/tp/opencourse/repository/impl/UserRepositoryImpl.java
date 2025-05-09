package com.tp.opencourse.repository.impl;

import com.tp.opencourse.dto.Page;
import com.tp.opencourse.entity.*;
import com.tp.opencourse.repository.UserRepository;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final LocalSessionFactoryBean factoryBean;

    @Override
    public void save(User user) {
        Session session = factoryBean.getObject().getCurrentSession();
        session.merge(user);
    }

    @Override
    public Optional<User> findById(String id) {
        Session session = factoryBean.getObject().getCurrentSession();
        User user = session.get(User.class, id);
        return user != null ? Optional.of(user) : Optional.empty();
    }

    @Override
    @Transactional
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
    public Page<User> findAllUserByRole(String role, String kw, int page, int size) {
        Session session = factoryBean.getObject().getCurrentSession();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);
        Join<User, Role> roleJoin = root.join("roles");

        query.select(root)
                .where(builder.equal(roleJoin.get("name"), role));
        Query q = session.createQuery(query);
        q.setFirstResult((page - 1) * size);
        q.setMaxResults(size);

        Long totalElement = countTeacher();
        List<User> teachers = q.getResultList();

        return com.tp.opencourse.dto.Page.<User>builder()
                .content(teachers)
                .totalElements(totalElement)
                .pageNumber(page)
                .pageSize(size)
                .totalPages((int) Math.ceil(totalElement * 1.0 / size))
                .build();
    }

    @Override
    public Long countTeacher() {
        Session session = factoryBean.getObject().getCurrentSession();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root<User> root = query.from(User.class);
        Join<User, Role> roleJoin = root.join("roles");

        query.select(builder.count(root))
                .where(builder.equal(roleJoin.get("name"), "TEACHER"));
        return session.createQuery(query).getSingleResult();
    }

    @Override
    public List<User> findAllUserInCourse(String courseId) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);

        Root<Register> registerRoot = query.from(Register.class);

        Join<Register, User> userJoin = registerRoot.join("student");

        Join<Register, RegisterDetail> registerDetailJoin = registerRoot.join("registerDetails");

        query.select(userJoin)
                .where(builder.equal(registerDetailJoin.get("course").get("id"), courseId));

        return session.createQuery(query).getResultList();

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