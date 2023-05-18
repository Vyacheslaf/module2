package com.epam.esm.repository;

import com.epam.esm.model.User;
import com.epam.esm.exception.dao.DaoWrongIdException;
import com.epam.esm.util.RequestParametersHolder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepositoryImpl extends AbstractRepository<User> {
    private static final String RESOURCE_NAME = "User";

    private final SessionFactory sessionFactory;

    @Autowired
    public UserRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<User> findAll(RequestParametersHolder rph) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("From User", User.class);
            query.setFirstResult(rph.getOffset());
            query.setMaxResults(rph.getSize());
            return query.list();
        }
    }

    @Override
    public User findById(long id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            if (user == null) {
                throw new DaoWrongIdException(id, RESOURCE_NAME);
            }
            return user;
        }
    }
}
