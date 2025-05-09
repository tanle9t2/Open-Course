package com.tp.opencourse.repository.impl;

import com.tp.opencourse.dto.Page;
import com.tp.opencourse.entity.*;
import com.tp.opencourse.repository.SubmitionRepository;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class SubmitionRepositoryImpl implements SubmitionRepository {
    @Autowired
    private LocalSessionFactoryBean factoryBean;

    @Override
    public Optional<Submition> findById(String id) {
        Session session = factoryBean.getObject().getCurrentSession();
        Submition submition = session.get(Submition.class, id);
        return submition != null ? Optional.of(submition) : Optional.empty();
    }

    @Override
    public Optional<Submition> findByContentProcess(String contentProcessId, String username) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Submition> q = b.createQuery(Submition.class);

        Root root = q.from(Submition.class);
        Join<Submition, ContentProcess> contentProcessJoin = root.join("content");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(b.equal(contentProcessJoin.get("id"), contentProcessId));
        predicates.add(b.equal(root.get("student").get("username"), username));

        q.select(root).where(predicates.toArray(new Predicate[0]));
        Submition submition = session.createQuery(q).getSingleResult();

        return submition != null ? Optional.of(submition) : Optional.empty();
    }

    @Override
    public List<Submition> findByContent(String contentId) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Submition> q = b.createQuery(Submition.class);
        Root root = q.from(Submition.class);
        Join<Submition, ContentProcess> join = root.join("content");
        q.select(root).where(b.equal(join.get("content").get("id"), contentId));
        Query query = session.createQuery(q);
        return query.getResultList();
    }

    private Long countSubmitions(String username, String courseId) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();

        CriteriaQuery<Long> q = builder.createQuery(Long.class);
        Root root = q.from(Submition.class);

        Join<Submition, ContentProcess> contentJoin = root.join("content");
        Join<ContentProcess, Content> contentProcessContentJoin = contentJoin.join("content");
        Join<Content, Section> sectionJoin = contentProcessContentJoin.join("section");

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(builder.equal(sectionJoin.get("course").get("teacher").get("username"), username));


        Optional.ofNullable(courseId).ifPresent(id ->
                predicates.add(builder.equal(sectionJoin.get("course").get("id"), id))
        );
        q.select(builder.count(root)).where(predicates.toArray(new Predicate[0]));

        return session.createQuery(q).getSingleResult();
    }

    @Override
    public Page<Submition> findByCourseId(String username, String courseId, int page, int size
            , String sortField, String order) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Submition> q = b.createQuery(Submition.class);
        Root root = q.from(Submition.class);
        Join<Submition, ContentProcess> contentJoin = root.join("content");
        Join<ContentProcess, Content> contentProcessContentJoin = contentJoin.join("content");
        Join<Content, Section> sectionJoin = contentProcessContentJoin.join("section");
        q.select(root);
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(b.equal(sectionJoin.get("course").get("teacher").get("username"), username));

        Optional.ofNullable(courseId).ifPresent(
                id -> predicates.add(b.equal(sectionJoin.get("course").get("id"), courseId)));
        Optional.ofNullable(sortField).ifPresent(
                field ->
                        q.orderBy(order.equals("ASC") ? b.asc(root.get(sortField)) : b.desc(root.get(sortField)))
        );
        q.where(predicates.toArray(new Predicate[0]));

        Query query = session.createQuery(q);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);

        Long totalElement = countSubmitions(username, courseId);
        List<Submition> submissions = query.getResultList();

        return Page.<Submition>builder()
                .content(submissions)
                .totalElements(totalElement)
                .pageNumber(page)
                .pageSize(size)
                .totalPages((int) Math.ceil(totalElement * 1.0 / size))
                .build();
    }

    @Override
    public Submition save(Submition submition) {
        Session s = this.factoryBean.getObject().getCurrentSession();
        return s.merge(submition);
    }
}
