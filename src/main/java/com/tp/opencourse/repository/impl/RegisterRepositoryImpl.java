package com.tp.opencourse.repository.impl;

import com.tp.opencourse.entity.*;
import com.tp.opencourse.entity.enums.PaymentStatus;
import com.tp.opencourse.entity.enums.RegisterStatus;
import com.tp.opencourse.repository.RegisterRepository;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public class RegisterRepositoryImpl implements RegisterRepository {

    @Autowired
    private LocalSessionFactoryBean factoryBean;

    @Override
    public List<Register> findAll() {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Register> query = builder.createQuery(Register.class);
        Root<Register> root = query.from(Register.class);

        query.select(root);
        return session.createQuery(query).getResultList();

    }

    @Override
    public void saveAll(List<Register> registers) {
        Session session = factoryBean.getObject().getCurrentSession();
        registers.forEach(session::merge);
    }

    @Override
    public long areCoursesRegisteredByUserId(String userId, List<String> courseIds) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root<Register> registerRoot = query.from(Register.class);
        Join<Register, RegisterDetail> registerDetailJoin = registerRoot.join("registerDetails");
        Join<RegisterDetail, Course> courseJoin = registerDetailJoin.join("course");

        Predicate inPredicate = courseJoin.get("id").in(courseIds);
        Predicate statusPredicate = builder.or(
                builder.equal(registerRoot.get("status"), RegisterStatus.SUCCESS),
                builder.equal(registerRoot.get("status"), RegisterStatus.PAYMENT_WAITING));
        Predicate userPredicate = builder.equal(registerRoot.get("student").get("id"), userId);

        query.select(builder.count(registerRoot)).where(
                builder.and(inPredicate, statusPredicate, userPredicate)
        );

        return session.createQuery(query).getSingleResult();
    }

    @Override
    public void save(Register register) {
        Session session = factoryBean.getObject().getCurrentSession();
        session.persist(register);
    }

    @Override
    public void update(Register register) {
        Session session = factoryBean.getObject().getCurrentSession();
        session.merge(register);
    }

    @Override
    public Optional<Register> findById(String id) {
        Session session = factoryBean.getObject().getCurrentSession();
        Register register = session.get(Register.class, id);
        return register != null ? Optional.of(register) : Optional.empty();

    }

    @Override
    public List<Register> findAllRegisteredCourse(String userId, RegisterStatus status) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Register> query = builder.createQuery(Register.class);

        Root<User> userRoot = query.from(User.class);
        Join<User, Register> registerJoin = userRoot.join("registers");
        Join<Register, RegisterDetail> registerDetailJoin = registerJoin.join("registerDetails");
        Join<RegisterDetail, Course> courseJoin = registerDetailJoin.join("course");

        query.select(registerJoin).where(
                builder.and(
                        builder.equal(userRoot.get("id"), userId),
                        builder.equal(registerJoin.get("status"), status)
                )
        );
        return session.createQuery(query).getResultList();
    }

    @Override
    public RegisterDetail findProgress(String userId, String courseId) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<RegisterDetail> query = builder.createQuery(RegisterDetail.class);

        Root<User> userRoot = query.from(User.class);
        Join<User, Register> registerJoin = userRoot.join("registers");
        Join<Register, RegisterDetail> registerDetailJoin = registerJoin.join("registerDetails");
        query.select(registerDetailJoin).where(
                builder.and(
                        builder.equal(userRoot.get("id"), userId),
                        builder.equal(registerJoin.get("status"), RegisterStatus.SUCCESS),
                        builder.equal(registerDetailJoin.get("course").get("id"), courseId)
                )
        );
        return session.createQuery(query).getSingleResult();
    }

    @Override
    public List<RegisterDetail> findAllLearnings(String userId) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<RegisterDetail> query = builder.createQuery(RegisterDetail.class);

        Root<User> userRoot = query.from(User.class);
        Join<User, Register> registerJoin = userRoot.join("registers");
        Join<Register, RegisterDetail> registerDetailJoin = registerJoin.join("registerDetails");
        query.select(registerDetailJoin).where(
                builder.and(
                        builder.equal(userRoot.get("id"), userId),
                        builder.equal(registerJoin.get("status"), RegisterStatus.SUCCESS)
                )
        );
        return session.createQuery(query).getResultList();
    }

    @Override
    public Long countTotalRegistration() {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root<Register> registerRoot = query.from(Register.class);

        query.select(builder.count(registerRoot))
                .where(builder.equal(registerRoot.get("status"), RegisterStatus.SUCCESS));

        Long res = session.createQuery(query).getSingleResult();
        return res != null ? res : 0L;
    }

    @Override
    public Double countTotalRevenue() {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Double> query = builder.createQuery(Double.class);

        Root<Register> registerRoot = query.from(Register.class);
        Join<Register, RegisterDetail> registerRegisterDetailJoin = registerRoot.join("registerDetails");

        query.select(builder.sum(registerRegisterDetailJoin.get("price")))
                .where(builder.and(
                        builder.equal(registerRoot.get("status"), RegisterStatus.SUCCESS)
                ));

        return session.createQuery(query).getSingleResult();
    }
}






