package com.tp.opencourse.repository.impl;

import com.tp.opencourse.entity.Course;
import com.tp.opencourse.entity.Rating;
import com.tp.opencourse.repository.RatingRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class RatingRepositoryImpl implements RatingRepository {
    private final LocalSessionFactoryBean factoryBean;

    @Override
    public void saveRating(Rating rating) {
        Session session = factoryBean.getObject().getCurrentSession();
        session.persist(rating);
    }

    @Override
    @Transactional
    public Object[] countRatingAverageAndQty(String courseId) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = builder.createQuery(Object[].class);

        Root<Rating> root = query.from(Rating.class);
        Join<Rating, Course> courseJoin = root.join("course");

        query.multiselect(
                builder.coalesce(builder.count(root), 0L),
                builder.coalesce(builder.avg(root.get("star")), 0.0))
            .where(builder.equal(courseJoin.get("id"), courseId));
        return session.createQuery(query).getSingleResult();
    }

    @Override
    public long isRatingExist(String courseId, String userId) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Rating> query = builder.createQuery(Rating.class);

        Root<Rating> root = query.from(Rating.class);
        query.select(root).where(
                builder.and(
                    builder.equal(root.get("course").get("id"), courseId),
                    builder.equal(root.get("user").get("id"), userId)
                )
        );
        return session.createQuery(query).setMaxResults(1).getResultList().size();
    }
}
