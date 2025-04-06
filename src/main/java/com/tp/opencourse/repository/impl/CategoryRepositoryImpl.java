package com.tp.opencourse.repository.impl;

import com.tp.opencourse.entity.Category;
import com.tp.opencourse.entity.Content;
import com.tp.opencourse.entity.Submition;
import com.tp.opencourse.repository.CategoryRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import org.checkerframework.checker.units.qual.A;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {
    @Autowired
    private LocalSessionFactoryBean factoryBean;

    @Override

    public List<Category> findFollowLevel(String parentName, int level) {
        Session session = factoryBean.getObject().getCurrentSession();
        String sql = """
                 SELECT node.id,node.name,node.lft,node.rgt
                 FROM category AS node
                 JOIN category AS parent ON node.lft BETWEEN parent.lft AND parent.rgt
                 JOIN category AS sub_parent ON node.lft BETWEEN sub_parent.lft AND sub_parent.rgt
                 JOIN (
                     SELECT node.name, (COUNT(parent.name) - 1) AS depth
                     FROM category AS node
                     JOIN category AS parent ON node.lft BETWEEN parent.lft AND parent.rgt
                     WHERE node.name = ?
                     GROUP BY node.name, node.lft
                 ) AS sub_tree
                 WHERE node.lft BETWEEN parent.lft AND parent.rgt
                         AND node.lft BETWEEN sub_parent.lft AND sub_parent.rgt
                         AND sub_parent.name = sub_tree.name
                 GROUP BY node.name, node.lft, sub_tree.depth,node.rgt,node.id
                 HAVING (COUNT(parent.name) - (sub_tree.depth + 1)) = ?
                 ORDER BY node.lft;
                """;
        Query<Category> query = session.createNativeQuery(sql, Category.class);
        // Set the parameters for the query
        query.setParameter(1, parentName);
        query.setParameter(2, level);

        return query.getResultList();
    }

    @Override
    public Optional<Category> findById(String id) {
        Session session = factoryBean.getObject().getCurrentSession();
        Category category = session.get(Category.class, id);
        return category != null ? Optional.of(category) : Optional.empty();
    }
}
