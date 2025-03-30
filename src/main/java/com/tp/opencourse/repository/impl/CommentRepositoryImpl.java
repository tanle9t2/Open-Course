package com.tp.opencourse.repository.impl;

import com.tp.opencourse.entity.Comment;
import com.tp.opencourse.repository.CommentRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CommentRepositoryImpl implements CommentRepository {
    @Autowired
    private LocalSessionFactoryBean factoryBean;

    @Override
    public void save(Comment comment) {
        Session session = factoryBean.getObject().getCurrentSession();
        session.merge(comment);
    }

    @Override
    public void delete(Comment comment) {
        Session session = factoryBean.getObject().getCurrentSession();
        session.remove(comment);
    }

    @Override
    public Optional<Comment> findById(String id) {
        Session session = factoryBean.getObject().getCurrentSession();
        Comment comment = session.get(Comment.class, id);
        return comment != null ? Optional.of(comment) : Optional.empty();
    }
}
