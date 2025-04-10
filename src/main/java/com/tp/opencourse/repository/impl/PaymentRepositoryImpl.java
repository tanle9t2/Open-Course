package com.tp.opencourse.repository.impl;

import com.tp.opencourse.entity.Payment;
import com.tp.opencourse.repository.PaymentRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;


@Repository
public class PaymentRepositoryImpl implements PaymentRepository {


    @Autowired
    private LocalSessionFactoryBean factoryBean;

    @Override
    public void save(Payment payment) {
        Session session = factoryBean.getObject().getCurrentSession();
        session.persist(payment);
    }

    @Override
    public void update(Payment payment) {
        Session session = factoryBean.getObject().getCurrentSession();
        session.merge(payment);
    }
}
