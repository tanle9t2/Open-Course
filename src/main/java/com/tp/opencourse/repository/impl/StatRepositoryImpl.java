package com.tp.opencourse.repository.impl;

import com.tp.opencourse.entity.Course;
import com.tp.opencourse.repository.StatRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StatRepositoryImpl implements StatRepository {
    private final LocalSessionFactoryBean factoryBean;

}
