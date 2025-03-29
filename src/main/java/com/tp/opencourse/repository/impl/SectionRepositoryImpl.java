package com.tp.opencourse.repository.impl;

import com.tp.opencourse.entity.Section;
import com.tp.opencourse.repository.SectionRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

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
    public void update(Section section) {
        Session s = this.factoryBean.getObject().getCurrentSession();
        s.merge(section);
    }

}