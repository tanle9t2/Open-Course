package com.tp.opencourse.repository.impl;

import com.tp.opencourse.entity.Course;
import com.tp.opencourse.repository.CourseRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class CourseRepositoryImpl implements CourseRepository {

    private final LocalSessionFactoryBean factoryBean;

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
}
