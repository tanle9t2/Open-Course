package com.tp.opencourse.repository.impl;

import com.tp.opencourse.entity.Category;
import com.tp.opencourse.entity.Content;
import com.tp.opencourse.repository.CategoryRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final LocalSessionFactoryBean factoryBean;

    @Override
    public List<String> getAllCategoryHierachyIds(int left, int right) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<String> query = builder.createQuery(String.class);

        Root<Category> root = query.from(Category.class);

        query.select(root.get("id")).where(
            builder.and(
                builder.lessThanOrEqualTo(root.get("lft"), left),
                builder.greaterThanOrEqualTo(root.get("rgt"), right)
            )
        );
        return session.createQuery(query).getResultList();
    }
}
