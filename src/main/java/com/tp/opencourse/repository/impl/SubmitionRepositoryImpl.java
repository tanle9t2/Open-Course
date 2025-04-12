package com.tp.opencourse.repository.impl;

import com.tp.opencourse.dto.Page;
import com.tp.opencourse.entity.Content;
import com.tp.opencourse.entity.Course;
import com.tp.opencourse.entity.Section;
import com.tp.opencourse.entity.Submition;
import com.tp.opencourse.repository.SubmitionRepository;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

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
    public List<Submition> findByContent(String contentId) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Submition> q = b.createQuery(Submition.class);
        Root root = q.from(Submition.class);
        Join<Submition, Content> join = root.join("content");
        q.select(root).where(b.equal(join.get("id"), contentId));
        Query query = session.createQuery(q);
        return query.getResultList();
    }

    private Long countSubmitions(String courseId) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();

        CriteriaQuery<Long> q = builder.createQuery(Long.class);
        Root root = q.from(Submition.class);

        Join<Submition, Content> contentJoin = root.join("content");
        Join<Content, Section> sectionJoin = contentJoin.join("section");

        q.select(root);
        q.select(builder.count(root))
                .where(builder.equal(sectionJoin.get("course").get("id"), courseId));

        return session.createQuery(q).getSingleResult();
    }

    @Override
    public Page<Submition> findByCourseId(String courseId, int page, int size
            , String sortField, String order) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Submition> q = b.createQuery(Submition.class);
        Root root = q.from(Submition.class);
        Join<Submition, Content> contentJoin = root.join("content");
        Join<Content, Section> sectionJoin = contentJoin.join("section");
        q.select(root);


        Optional.ofNullable(courseId).ifPresent(
                id -> q.where(b.equal(sectionJoin.get("course").get("id"), courseId)));
        Optional.ofNullable(sortField).ifPresent(
                field ->
                        q.orderBy(order.equals("ASC") ? b.asc(root.get(sortField)) : b.desc(root.get(sortField)))
        );
        Query query = session.createQuery(q);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);

        Long totalElement = countSubmitions(courseId);
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
    public void update(Submition submition) {
        Session s = this.factoryBean.getObject().getCurrentSession();
        s.merge(submition);
    }
}
