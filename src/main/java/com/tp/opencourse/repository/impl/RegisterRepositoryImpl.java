package com.tp.opencourse.repository.impl;

import com.tp.opencourse.entity.Content;
import com.tp.opencourse.entity.Course;
import com.tp.opencourse.entity.Register;
import com.tp.opencourse.entity.RegisterDetail;
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
    public long areCoursesRegisteredByUserId(String userId, List<String> courseIds) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root<Register> registerRoot = query.from(Register.class);
        Join<Register, RegisterDetail> registerDetailJoin = registerRoot.join("registerDetails");
        Join<RegisterDetail, Course> courseJoin = registerDetailJoin.join("course");

        Predicate inPredicate = courseJoin.get("id").in(courseIds);
        Predicate statusPredicate = builder.equal(registerRoot.get("status"), RegisterStatus.SUCCESS);
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
    public Optional<Register> findById(String id) {
        Session session = factoryBean.getObject().getCurrentSession();
        Register register = session.get(Register.class, id);
        return register != null ? Optional.of(register) : Optional.empty();

    }
}






