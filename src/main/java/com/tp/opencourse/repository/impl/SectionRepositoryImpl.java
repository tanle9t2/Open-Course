package com.tp.opencourse.repository.impl;

import com.tp.opencourse.entity.Register;
import com.tp.opencourse.entity.RegisterDetail;
import com.tp.opencourse.entity.Section;
import com.tp.opencourse.entity.User;
import com.tp.opencourse.entity.enums.RegisterStatus;
import com.tp.opencourse.repository.SectionRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SectionRepositoryImpl implements SectionRepository {
    @Autowired
    private LocalSessionFactoryBean factoryBean;

    @Override
    public Optional<Section> findById(String id) {
        Session session = factoryBean.getObject().getCurrentSession();
        Section section = session.get(Section.class, id);
        return section != null ? Optional.of(section) : Optional.empty();
    }

    @Override
    public List<Section> findByCourseId(String courseId) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Section> query = builder.createQuery(Section.class);

        Root<Section> sectionRoot = query.from(Section.class);

        query.select(sectionRoot).where(builder.equal(sectionRoot.get("course").get("id"), courseId));

        return session.createQuery(query).getResultList();
    }

    @Override
    public Section update(Section section) {
        Session s = this.factoryBean.getObject().getCurrentSession();
        return s.merge(section);
    }

    @Override
    public Section create(Section section) {
        Session session = this.factoryBean.getObject().getCurrentSession();
        return session.merge(section);
    }

    @Override
    public void removeSection(Section section) {
        Session session = this.factoryBean.getObject().getCurrentSession();
        session.remove(section);
    }

}