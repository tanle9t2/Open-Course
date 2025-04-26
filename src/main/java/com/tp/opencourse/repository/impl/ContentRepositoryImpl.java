package com.tp.opencourse.repository.impl;

import com.tp.opencourse.entity.*;
import com.tp.opencourse.mapper.ContentMapper;
import com.tp.opencourse.repository.ContentRepository;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ContentRepositoryImpl implements ContentRepository {
    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public Optional<Content> findContentById(String uuid) {
        Session session = factory.getObject().getCurrentSession();
        Content content = session.get(Content.class, uuid);
        return content != null ? Optional.of(content) : Optional.empty();
    }

    @Override
    public void remove(Content content) {
        Session session = factory.getObject().getCurrentSession();
        session.remove(content);
    }

    @Override
    public Content updateContent(Content content) {
        Session session = factory.getObject().getCurrentSession();
        return session.merge(content);
    }

    @Override
    public List<ContentProcess> countContentComplete(String sectionId, String userId) {
        Session session = factory.getObject().getCurrentSession();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<ContentProcess> query = builder.createQuery(ContentProcess.class);

        Root<Section> root = query.from(Section.class);
        Join<Section, Content> contentJoin = root.join("contentList");
        Join<Content, ContentProcess> contentContentProcessJoin = contentJoin.join("contentProcesses");
        Join<ContentProcess, RegisterDetail> registerDetailJoin = contentContentProcessJoin.join("registerDetail");
        Join<RegisterDetail, Register> registerJoin = registerDetailJoin.join("register");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get("id"), sectionId));
        predicates.add(builder.equal(registerJoin.get("status"), "SUCCESS"));
        predicates.add(builder.equal(registerJoin.get("student").get("username"), userId));


        query.select(contentContentProcessJoin).where(predicates.toArray(new Predicate[0]))
                .orderBy(builder.desc(contentJoin.get("createdAt")));
        return session.createQuery(query).getResultList();
    }
}
