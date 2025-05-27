package com.tp.opencourse.repository.impl;

import com.tp.opencourse.entity.Course;
import com.tp.opencourse.entity.RegisterDetail;
import com.tp.opencourse.entity.Role;
import com.tp.opencourse.entity.User;
import com.tp.opencourse.repository.RegisterDetailRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegisterDetailRepositoryImpl implements RegisterDetailRepository {
    @Autowired
    private LocalSessionFactoryBean factoryBean;

    @Override
    public void update(RegisterDetail registerDetail) {
        Session session = factoryBean.getObject().getCurrentSession();
        session.merge(registerDetail);
    }

    @Override
    public List<RegisterDetail> findAllByCourseId(String courseId) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<RegisterDetail> query = builder.createQuery(RegisterDetail.class);
        Root<RegisterDetail> root = query.from(RegisterDetail.class);

        query.select(root)
                .where(builder.and(
                        builder.equal(root.get("course").get("id"), courseId)
                ));

        return session.createQuery(query).getResultList();
    }

    @Override
    public Double sumRevenueOfTeacher(String teacherId) {
        Session session = factoryBean.getObject().getCurrentSession();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Double> query = builder.createQuery(Double.class);

        Root<RegisterDetail> root = query.from(RegisterDetail.class);
        Join<RegisterDetail, Course> courseJoin = root.join("course");
        Join<Course, User> userJoin = courseJoin.join("teacher");

        query.select(builder.sum(courseJoin.get("price")))
                .where(builder.equal(userJoin.get("id"), teacherId));
        return session.createQuery(query).getSingleResult();
    }
}
