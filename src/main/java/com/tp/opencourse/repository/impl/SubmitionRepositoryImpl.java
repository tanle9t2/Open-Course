package com.tp.opencourse.repository.impl;

import com.tp.opencourse.entity.Content;
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

    @Override
    public void update(Submition submition) {
        Session s = this.factoryBean.getObject().getCurrentSession();
        s.merge(submition);
    }
}
