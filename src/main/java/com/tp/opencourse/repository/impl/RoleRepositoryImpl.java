package com.tp.opencourse.repository.impl;

import com.tp.opencourse.entity.Role;
import com.tp.opencourse.repository.RoleRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository {
    private final LocalSessionFactoryBean factoryBean;

    @Override
    public List<Role> findDefaultRolesForNewlyLoggedInUser() {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();

        CriteriaQuery<Role> query = builder.createQuery(Role.class);
        Root<Role> root = query.from(Role.class);
        Predicate predicate = root.get("name").in(List.of("STUDENT", "TEACHER"));

        query.select(root).where(predicate);

        return session.createQuery(query).getResultList();
    }

    @Override
    public List<Role> findAll() {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();

        CriteriaQuery<Role> query = builder.createQuery(Role.class);
        Root<Role> root = query.from(Role.class);

        query.select(root);

        return session.createQuery(query).getResultList();
    }

    @Override
    public Role findByName(String name) {
        Session session = factoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();

        CriteriaQuery<Role> query = builder.createQuery(Role.class);
        Root<Role> root = query.from(Role.class);

        query.select(root).where(builder.equal(root.get("name"), name));

        return session.createQuery(query).getSingleResult();
    }
}














