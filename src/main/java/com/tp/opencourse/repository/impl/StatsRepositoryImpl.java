package com.tp.opencourse.repository.impl;

import com.tp.opencourse.entity.Course;
import com.tp.opencourse.entity.Register;
import com.tp.opencourse.entity.RegisterDetail;
import com.tp.opencourse.entity.User;
import com.tp.opencourse.entity.enums.RegisterStatus;
import com.tp.opencourse.repository.StatsRepository;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StatsRepositoryImpl implements StatsRepository {
    private final LocalSessionFactoryBean factoryBean;

    @Override
    public List<Object[]> getStatisticsByPeriod(String year, String periodType) {

        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = builder.createQuery(Object[].class);

        Root<Register> root = query.from(Register.class);
        Join<Register, RegisterDetail> registerDetailJoin = root.join("registerDetails");

        Predicate yearPredicate = builder.equal(builder.function("YEAR", Integer.class, root.get("createdAt")), year);

        query.multiselect(
                builder.function(periodType, String.class, root.get("createdAt")),
                builder.sum(registerDetailJoin.get("price"))
        );
        query.groupBy(builder.function(periodType, String.class, root.get("createdAt")));
        query.orderBy(builder.asc(builder.function(periodType, String.class, root.get("createdAt"))));

        query.where(yearPredicate);

        Query q = session.createQuery(query);
        return q.getResultList();
    }

    @Override
    public List<Object[]> getCourseRevenuStatistics(Integer offset) {
        Session session = factoryBean.getObject().getCurrentSession();

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

        Root<RegisterDetail> rd = cq.from(RegisterDetail.class);
        Join<RegisterDetail, Course> course = rd.join("course");
        Join<RegisterDetail, Register> register = rd.join("register");

        // Extract current date and compute last month
        LocalDate today = LocalDate.now();
        LocalDate lastMonth = today.minusMonths(offset);

        Expression<Integer> monthExpr = cb.function("MONTH", Integer.class, register.get("createdAt"));
        Expression<Integer> targetMonth = cb.literal(lastMonth.getMonthValue());
        Expression<Integer> yearExpr = cb.function("YEAR", Integer.class, register.get("createdAt"));
        Expression<Integer> targetYear = cb.literal(lastMonth.getYear());

        cq.multiselect(
                        course.get("id"),
                        cb.coalesce(cb.sum(rd.get("price")), 0.0)
                )
                .where(
                        cb.and(
                                cb.equal(register.get("status"), RegisterStatus.SUCCESS),
                                cb.equal(monthExpr, targetMonth),
                                cb.equal(yearExpr, targetYear)
                        )
                )
                .groupBy(course.get("id"))
                .orderBy(cb.asc(course.get("id")));

        return session.createQuery(cq).getResultList();
    }
}
