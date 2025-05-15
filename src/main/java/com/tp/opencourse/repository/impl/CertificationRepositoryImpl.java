package com.tp.opencourse.repository.impl;

import com.tp.opencourse.entity.Certification;
import com.tp.opencourse.entity.Course;
import com.tp.opencourse.entity.Rating;
import com.tp.opencourse.repository.CertificationRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class CertificationRepositoryImpl implements CertificationRepository {

    @Autowired
    private LocalSessionFactoryBean factoryBean;

    @Override
    public Optional<Certification> findById(String id) {
        Session session = factoryBean.getObject().getCurrentSession();
        Certification certification = session.get(Certification.class, id);
        return certification != null ? Optional.of(certification) : Optional.empty();
    }

    @Override
    public Optional<Certification> findByIdRegisterId(String rgId) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Certification> query = builder.createQuery(Certification.class);
        Root<Certification> root = query.from(Certification.class);
        Join<Certification, Certification> join = root.join("registerDetail");
        query.select(root)
                .where(builder.equal(join.get("id"), rgId));

        List<Certification> results = session.createQuery(query).getResultList();

        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Certification save(Certification certification) {
        Session session = factoryBean.getObject().getCurrentSession();
        return session.merge(certification);
    }
//
//    @Override
//    public List<String> findAllByUserId(String id) {
//        Session session = factoryBean.getObject().getCurrentSession();
//        CriteriaBuilder builder = session.getCriteriaBuilder();
//        CriteriaQuery<String> query = builder.createQuery(String.class);
//
//        Root<Certification> root = query.from(Certification.class);
//
//        query.select(root.get("course").get("id")).where(builder.equal(root.get("user").get("id"), id));
//        return session.createQuery(query).getResultList();
//    }
}
