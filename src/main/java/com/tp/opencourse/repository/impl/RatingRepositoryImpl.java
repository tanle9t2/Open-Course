package com.tp.opencourse.repository.impl;

import com.tp.opencourse.dto.Page;
import com.tp.opencourse.entity.Course;
import com.tp.opencourse.entity.Rating;
import com.tp.opencourse.entity.Register;
import com.tp.opencourse.entity.RegisterDetail;
import com.tp.opencourse.repository.RatingRepository;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        Join<Rating, RegisterDetail> registerDetailJoin = root.join("registerDetail");

        query.multiselect(
                builder.coalesce(builder.count(root), 0L),
                builder.coalesce(builder.avg(root.get("star")), 0.0))
            .where(builder.equal(registerDetailJoin.get("course").get("id"), courseId));
        return session.createQuery(query).getSingleResult();
    }

    @Override
    public List<Object[]> findRatingSummaryByCourseId(String courseId) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = builder.createQuery(Object[].class);

        Root<Rating> root = query.from(Rating.class);
        Join<Rating, RegisterDetail> registerDetailJoin = root.join("registerDetail");

        query.multiselect(root.get("star"), builder.count(root))
                .where(builder.equal(registerDetailJoin.get("course").get("id"), courseId))
                .groupBy(root.get("star"))
                .orderBy(builder.asc(root.get("star")));

        return session.createQuery(query).getResultList();
    }

    @Override
    public Optional<Rating> isRatingExist(String courseId, String userId) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Rating> query = builder.createQuery(Rating.class);

        Root<Rating> root = query.from(Rating.class);
        Join<Rating, RegisterDetail> registerDetailJoin = root.join("registerDetail");
        Join<RegisterDetail, Register> registerJoin = registerDetailJoin.join("register");

        query.select(root).where(
                builder.and(
                    builder.equal(registerDetailJoin.get("course").get("id"), courseId),
                    builder.equal(registerJoin.get("student").get("id"), userId)
                )
        );
        Rating rating = session.createQuery(query).getSingleResultOrNull();
        return rating != null ? Optional.of(rating) : Optional.empty();
    }

    @Override
    public Page<Rating> findRatingByCourseId(String courseId, Integer page, Integer size, Integer starCount) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Rating> query = builder.createQuery(Rating.class);

        Root<Rating> root = query.from(Rating.class);
        Join<Rating, RegisterDetail> registerDetailJoin = root.join("registerDetail");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(registerDetailJoin.get("course").get("id"), courseId));
        if(starCount != null) {
            predicates.add(builder.equal(root.get("star"), starCount));
        }
        query.select(root).where(predicates.toArray(new Predicate[0]));
        Query q = session.createQuery(query);
        if(page != null && size != null) {
            q.setFirstResult((page - 1) * size);
            q.setMaxResults(size);
        }

        List<Rating> ratings = q.getResultList();

        long totalElement = countRatingByCourseId(courseId, starCount);
        return Page.<Rating>builder()
                .content(ratings)
                .totalElements(totalElement)
                .pageNumber(page)
                .pageSize(size)
                .totalPages((int) Math.ceil(totalElement * 1.0 / size))
                .build();
    }

    @Override
    public Optional<Rating> findRatingByCourseIdAndUserId(String courseId, String userId) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Rating> query = builder.createQuery(Rating.class);

        Root<Rating> root = query.from(Rating.class);
        Join<Rating, RegisterDetail> registerDetailJoin = root.join("registerDetail");
        Join<RegisterDetail, Register> registerJoin = registerDetailJoin.join("register");

        query.select(root).where(builder.and(
                builder.equal(registerDetailJoin.get("course").get("id"), courseId),
                builder.equal(registerJoin.get("student").get("id"), userId)
        ));
        Rating rating = session.createQuery(query).getSingleResultOrNull();
        return rating != null ? Optional.of(rating) : Optional.empty();
    }

    @Override
    public Optional<Rating> findById(String courseId) {
        Session session = factoryBean.getObject().getCurrentSession();
        Rating rating = session.get(Rating.class, courseId);
        return rating != null ? Optional.of(rating) : Optional.empty();
    }

    @Override
    public long countRatingByCourseId(String courseId, Integer starCount) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root<Rating> root = query.from(Rating.class);
        Join<Rating, RegisterDetail> registerDetailJoin = root.join("registerDetail");

        query.select(builder.count(root))
                .where(builder.equal(registerDetailJoin.get("course").get("id"), courseId))
                .orderBy(builder.asc(root.get("star")));
        if(starCount != null) {
            query.where(builder.equal(root.get("star"), starCount));
        }

        return session.createQuery(query).getSingleResult();
    }

    @Override
    public void delete(Rating rating) {
        Session session = factoryBean.getObject().getCurrentSession();
        session.remove(rating);
    }
}
