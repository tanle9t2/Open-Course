package com.tp.opencourse.repository.impl;

import com.tp.opencourse.dto.Page;
import com.tp.opencourse.entity.Register;
import com.tp.opencourse.entity.RegisterDetail;
import com.tp.opencourse.entity.User;
import com.tp.opencourse.repository.UserRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.query.Query;
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
    public Page<User> findAll(String keyword, int page, int size, String sortBy, String direction) {
        Session session = factoryBean.getObject().openSession();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);

        query.select(root);
        if(keyword != null) {
            query.where(builder.or(
                    builder.like(root.get("id"), String.format("%%%s%%", keyword)),
                    builder.like(root.get("username"), String.format("%%%s%%", keyword)),
                    builder.like(root.get("firstName"), String.format("%%%s%%", keyword)),
                    builder.like(root.get("lastName"), String.format("%%%s%%", keyword))
            ));
        }
        query.orderBy(builder.desc(root.get("createdAt")));
//        if(sortBy != null) {
//            query.orderBy(direction.equals("asc") ? builder.asc(root.get(sortBy)) : builder.desc(root.get(sortBy)));
//        }

        List<User> users = session.createQuery(query)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();

        Long totalElement = count();
        return Page.<User>builder()
                .content(users)
                .totalElements(totalElement)
                .pageNumber(page)
                .pageSize(size)
                .totalPages((int) Math.ceil(totalElement * 1.0 / size))
                .build();
    }

    @Override
    public Long count() {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();

        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<User> root = query.from(User.class);

        query.select(builder.count(root));

        return session.createQuery(query).getSingleResult();
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
    @Transactional
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        Session session = factoryBean.getObject().openSession();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);

        query.select(root).where(
                builder.or(
                        builder.equal(root.get("username"), usernameOrEmail),
                        builder.equal(root.get("email"), usernameOrEmail)
                )

        );

        User user = session.createQuery(query).uniqueResult();
        return user != null ? Optional.of(user) : Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Session session = factoryBean.getObject().getCurrentSession();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);

        query.select(root).where(
                builder.equal(root.get("email"), email)
        );

        User user = session.createQuery(query).uniqueResult();
        return user != null ? Optional.of(user) : Optional.empty();
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