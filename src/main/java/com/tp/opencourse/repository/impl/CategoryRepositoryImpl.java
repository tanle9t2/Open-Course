package com.tp.opencourse.repository.impl;

import com.tp.opencourse.entity.Category;
import com.tp.opencourse.entity.Content;
import com.tp.opencourse.repository.CategoryRepository;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
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

    @Override
    public List<Category> getRootCategory() {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Category> query = builder.createQuery(Category.class);

        Root<Category> node = query.from(Category.class);
        Root<Category> parent = query.from(Category.class);

        Predicate between = builder.between(node.get("lft"), parent.get("lft"), parent.get("rgt"));

        query.groupBy(node.get("id"), node.get("name"), node.get("lft"));

        Expression<Long> count = builder.count(parent.get("name"));
        Expression<Integer> depth = builder.diff(count.as(Integer.class), 1);

        query.having(builder.equal(depth, 0));

        query.select(node);

        query.where(between);

        query.orderBy(builder.asc(node.get("lft")));

        return session.createQuery(query).getResultList();
    }

    @Override
    public List<Category> getCategoryByLevel(String parentId) {
        Session session = factoryBean.getObject().getCurrentSession();
        String sql = """
                SELECT node.*
                    FROM    category AS node,
                            category AS parent,
                            category AS sub_parent,
                            (
                                    SELECT node.id, (COUNT(parent.id) - 1) AS depth
                                    FROM category AS node,
                                    category AS parent
                                    WHERE node.lft BETWEEN parent.lft AND parent.rgt
                                    AND node.id = :parent_id
                                    GROUP BY node.id
                                    order by node.lft
                            ) AS sub_tree
                    WHERE node.lft BETWEEN parent.lft AND parent.rgt
                            AND node.lft BETWEEN sub_parent.lft AND sub_parent.rgt
                            AND sub_parent.id = sub_tree.id
                    GROUP BY node.id, sub_tree.depth
                    HAVING (COUNT(parent.id) - (sub_tree.depth + 1))  = 1
                    order by node.lft
                """;
        NativeQuery<Category> query = session.createNativeQuery(sql, Category.class);
        query.setParameter("parent_id", parentId);

        return query.list();
    }
}
