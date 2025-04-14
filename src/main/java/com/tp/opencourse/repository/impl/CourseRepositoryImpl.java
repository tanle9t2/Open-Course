package com.tp.opencourse.repository.impl;

import com.tp.opencourse.entity.*;
import com.tp.opencourse.entity.enums.RegisterStatus;
import com.tp.opencourse.dto.Page;
import com.tp.opencourse.entity.Course;
import com.tp.opencourse.entity.Submition;
import com.tp.opencourse.repository.CourseRepository;
import jakarta.persistence.criteria.*;
import jakarta.servlet.Registration;
import lombok.RequiredArgsConstructor;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.checkerframework.checker.units.qual.A;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CourseRepositoryImpl implements CourseRepository {
    @Autowired
    private LocalSessionFactoryBean factoryBean;

    @Override
    public Optional<Course> findById(String id) {
        Session session = factoryBean.getObject().getCurrentSession();
        Course course = session.get(Course.class, id);
        return course != null ? Optional.of(course) : Optional.empty();
    }

    @Override
    public Course create(Course course) {
        Session session = factoryBean.getObject().getCurrentSession();
        return session.merge(course);
    }

    private final LocalSessionFactoryBean factoryBean;
    @Override
    public Course update(Course course) {
        Session session = factoryBean.getObject().getCurrentSession();
        return session.merge(course);
    }
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
    public void delete(String id) {
        Session session = factoryBean.getObject().getCurrentSession();
        Course course = session.get(Course.class, id);
        session.remove(course);
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


    @Override
    public Page<Course> findByTeacherId(String id, String kw, int page, int limit) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Course> q = b.createQuery(Course.class);
        Root root = q.from(Course.class);

        q.select(root);
        ;

        List<Predicate> predicates = new ArrayList<>();
        Predicate equalTeacher = b.equal(root.get("teacher").get("id"), id);
        predicates.add(equalTeacher);

        Optional.ofNullable(kw).ifPresent(v -> predicates.add(b.like(root.get("name"), String.format("%%%s%%", v))));
        q.where(predicates.toArray(new Predicate[0]));

        Query query = session.createQuery(q);
        query.setFirstResult((page - 1) * limit);
        query.setMaxResults(limit);

        Long totalElement = countByTeacherId(id);
        List<Course> courses = query.getResultList();

        return Page.<Course>builder()
                .content(courses)
                .totalElements(totalElement)
                .pageNumber(page)
                .pageSize(limit)
                .totalPages((int) Math.ceil(totalElement * 1.0 / limit))
                .build();
    }

    @Override
    public List<Course> findByTeacherId(String id) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Course> q = b.createQuery(Course.class);
        Root root = q.from(Course.class);

        q.select(root);
        q.where(b.equal(root.get("teacher").get("id"), id));

        Query query = session.createQuery(q);
        return query.getResultList();
    }

    @Override
    public Long countByTeacherId(String id) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();

        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Course> root = query.from(Course.class);

        query.select(builder.count(root))
                .where(builder.equal(root.get("teacher").get("id"), id));

        return session.createQuery(query).getSingleResult();
    }
}
