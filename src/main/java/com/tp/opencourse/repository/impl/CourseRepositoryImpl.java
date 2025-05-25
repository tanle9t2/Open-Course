package com.tp.opencourse.repository.impl;

import com.tp.opencourse.entity.*;
import com.tp.opencourse.entity.enums.CourseStatus;
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
    public long count() {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root<Course> root = query.from(Course.class);
        query.select(builder.count(root));
        return session.createQuery(query).getSingleResult();
    }

    @Override
    public long countInStatus(String kw, CourseStatus status) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root<Course> root = query.from(Course.class);
        Optional.ofNullable(kw).ifPresent(
                keyword -> query.where(builder.like(root.get("name"), String.format("%%%s%%", keyword))));
        query.select(builder.count(root))
                .where(builder.equal(root.get("status"), status));
        return session.createQuery(query).getSingleResult();
    }

    @Override
    public long countByKw(String kw) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Course> root = query.from(Course.class);
        Optional.ofNullable(kw).ifPresent(
                keyword -> query.where(builder.like(root.get("name"), String.format("%%%s%%", keyword))));

        query.select(builder.count(root));
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
    public Page<Course> findAll(String keyword, int page, int size, String sortBy, String direction) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Course> q = builder.createQuery(Course.class);
        Root<Course> root = q.from(Course.class);
        q.select(root);
        Optional.ofNullable(keyword)
                .filter(kw -> !kw.trim().isEmpty())  // Skip null and empty strings
                .ifPresent(kw ->
                        q.where(builder.like(root.get("name"), "%" + kw + "%"))
                );

        Optional.ofNullable(sortBy)
                .filter(field -> !field.trim().isEmpty())
                .ifPresent(
                        field ->
                                q.orderBy(direction.equals("ASC") ? builder.asc(root.get(field)) : builder.desc(root.get(field)))
                );
        Query query = session.createQuery(q);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);

        Long totalElement = this.countByKw(keyword);
        List<Course> courses = query.getResultList();

        return Page.<Course>builder()
                .content(courses)
                .totalElements(totalElement)
                .pageNumber(page)
                .pageSize(size)
                .totalPages((int) Math.ceil(totalElement * 1.0 / size))
                .build();
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
    public Page<Course> findAllInActive(String keyword, int page, int size, String sortBy, String direction) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Course> q = builder.createQuery(Course.class);
        Root<Course> root = q.from(Course.class);
        q.select(root);
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(builder.equal(root.get("status"), CourseStatus.PENDING));
        Optional.ofNullable(keyword).ifPresent(
                kw -> predicates.add(builder.like(root.get("name"), String.format("%%%s%%", kw))));

        Optional.ofNullable(sortBy)
                .filter(field -> !field.isEmpty())
                .ifPresent(
                        field ->
                                q.orderBy(direction.equals("ASC") ? builder.asc(root.get(sortBy)) : builder.desc(root.get(sortBy)))
                );

        q.where(predicates.toArray(new Predicate[0]));
        Query query = session.createQuery(q);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);

        Long totalElement = this.countInStatus(keyword, CourseStatus.PENDING);
        List<Course> courses = query.getResultList();

        return Page.<Course>builder()
                .content(courses)
                .totalElements(totalElement)
                .pageNumber(page)
                .pageSize(size)
                .totalPages((int) Math.ceil(totalElement * 1.0 / size))
                .build();
    }


    @Override
    public Page<Course> findByTeacherId(String id, String kw, int page, int limit) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Course> q = b.createQuery(Course.class);
        Root root = q.from(Course.class);
        q.select(root);
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(b.notEqual(root.get("status"), CourseStatus.DELETE));
        Predicate equalTeacher = b.equal(root.get("teacher").get("username"), id);
        predicates.add(equalTeacher);

        Optional.ofNullable(kw).ifPresent(v -> predicates.add(b.like(root.get("name"), String.format("%%%s%%", v))));
        q.where(predicates.toArray(new Predicate[0]));

        Query query = session.createQuery(q);
        query.setFirstResult((page - 1) * limit);
        query.setMaxResults(limit);

        Long totalElement = countByTeacherId(id, kw);
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
        q.where(b.and(
                b.notEqual(root.get("status"), CourseStatus.DELETE),
                b.equal(root.get("teacher").get("id"), id)));

        Query query = session.createQuery(q);
        return query.getResultList();
    }

    @Override
    public Long countByTeacherId(String username, String kw) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();

        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Course> root = query.from(Course.class);

        //set predicate
        List<Predicate> predicates = new ArrayList<>();
        Optional.ofNullable(kw).ifPresent(v -> predicates.add(builder.like(root.get("name"), String.format("%%%s%%", v))));
        predicates.add(builder.equal(root.get("teacher").get("username"), username));
        predicates.add(builder.notEqual(root.get("status"), CourseStatus.DELETE));

        //build query
        query.select(builder.count(root))
                .where(predicates.toArray(new Predicate[0]));

        return session.createQuery(query).getSingleResult();
    }

    @Override
    public boolean isCourseRegistered(String userId, String courseId) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root<User> userRoot = query.from(User.class);
        Join<User, Register> registerJoin = userRoot.join("registers");
        Join<Register, RegisterDetail> registerRegisterDetailJoin = registerJoin.join("registerDetails");

        query.select(builder.count(userRoot)).where(
                builder.and(
                        builder.equal(userRoot.get("id"), userId),
                        builder.equal(registerRegisterDetailJoin.get("course").get("id"), courseId),
                        builder.or(
                                builder.equal(registerJoin.get("status"), RegisterStatus.SUCCESS),
                                builder.equal(registerJoin.get("status"), RegisterStatus.PAYMENT_WAITING)
                        )
                )
        );
        return session.createQuery(query).getSingleResult() > 0;
    }

    @Override
    public boolean isCoursePaid(String username, String courseId) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root<User> userRoot = query.from(User.class);
        Join<User, Register> registerJoin = userRoot.join("registers");
        Join<Register, RegisterDetail> registerRegisterDetailJoin = registerJoin.join("registerDetails");

        query.select(builder.count(userRoot)).where(
                builder.and(
                        builder.equal(userRoot.get("username"), username),
                        builder.equal(registerRegisterDetailJoin.get("course").get("id"), courseId),
                        builder.or(
                                builder.equal(registerJoin.get("status"), RegisterStatus.SUCCESS)
                        )
                )
        );
        return session.createQuery(query).getSingleResult() > 0;
    }
}
