package com.tp.opencourse.repository.impl;

import com.tp.opencourse.entity.*;
import com.tp.opencourse.entity.enums.RegisterStatus;
import com.tp.opencourse.repository.CourseRepository;
import jakarta.persistence.criteria.*;
import jakarta.servlet.Registration;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Repository
@RequiredArgsConstructor
public class CourseRepositoryImpl implements CourseRepository {

    private final LocalSessionFactoryBean factoryBean;

    @Override
    public boolean isCourseExisted(String courseId) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Boolean> query = builder.createQuery(Boolean.class);

        Subquery<Course> subquery = query.subquery(Course.class);
        Root<Course> subRoot = subquery.from(Course.class);
        subquery.select(subRoot).where(builder.equal(subRoot.get("id"), courseId));

        query.select(builder.exists(subquery));
        return session.createQuery(query).getSingleResult();
    }

    @Override
    @Transactional
    public long countTotalLecture(String courseId) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root<Content> contentRoot = query.from(Content.class);
        Join<Content, Section> sectionJoin = contentRoot.join("section");
        Join<Section, Course> courseJoin = sectionJoin.join("course");

        query.select(builder.count(contentRoot))
                .where(builder.equal(courseJoin.get("id"), courseId));

        return session.createQuery(query).getSingleResult();
    }

    @Override
    @Transactional
    public long countTotalRegistration(String courseId) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root<Register> registerRoot = query.from(Register.class);
        Join<Register, RegisterDetail> registerRegisterDetailJoin = registerRoot.join("registerDetails");
        Join<RegisterDetail, Course> courseJoin = registerRegisterDetailJoin.join("course");

        query.select(builder.count(registerRoot))
                .where(builder.and(
                        builder.equal(courseJoin.get("id"), courseId),
                        builder.equal(registerRoot.get("status"), RegisterStatus.SUCCESS)
                ));

        return session.createQuery(query).getSingleResult();
    }

    @Override
    public List<Course> findAllByIds(Set<String> courseIds) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Course> query = builder.createQuery(Course.class);
        Root<Course> root = query.from(Course.class);

        CriteriaBuilder.In<String> inClause = builder.in(root.get("id"));
        for (String id : courseIds) {
            inClause.value(id);
        }

        query.select(root).where(inClause);
        return session.createQuery(query).getResultList();
    }

    @Override
    public List<Course> findAll() {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Course> query = builder.createQuery(Course.class);
        Root<Course> root = query.from(Course.class);

        query.select(root);
        return session.createQuery(query).getResultList();
    }
}
