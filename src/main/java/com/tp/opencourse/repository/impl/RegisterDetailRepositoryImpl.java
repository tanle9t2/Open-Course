package com.tp.opencourse.repository.impl;

import com.tp.opencourse.entity.RegisterDetail;
import com.tp.opencourse.repository.RegisterDetailRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public class RegisterDetailRepositoryImpl implements RegisterDetailRepository {
    @Autowired
    private LocalSessionFactoryBean factoryBean;

    @Override
    public void update(RegisterDetail registerDetail) {
        Session session = factoryBean.getObject().getCurrentSession();
        session.merge(registerDetail);
    }
}
