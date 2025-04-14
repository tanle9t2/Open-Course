package com.tp.opencourse.repository.impl;

import com.tp.opencourse.dto.Page;
import com.tp.opencourse.entity.Course;
import com.tp.opencourse.entity.Submition;
import com.tp.opencourse.repository.CourseRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
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

    @Override
    public Course update(Course course) {
        Session session = factoryBean.getObject().getCurrentSession();
        return session.merge(course);
    }

    @Override
    public void delete(String id) {
        Session session = factoryBean.getObject().getCurrentSession();
        Course course = session.get(Course.class, id);
        session.remove(course);
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
