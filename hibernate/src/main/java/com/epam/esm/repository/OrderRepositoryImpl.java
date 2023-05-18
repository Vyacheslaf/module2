package com.epam.esm.repository;

import com.epam.esm.exception.dao.DaoWrongOrderIdForUserException;
import com.epam.esm.model.Order;
import com.epam.esm.model.dto.GiftCertificatePrice;
import jakarta.persistence.NoResultException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderRepositoryImpl extends AbstractRepository<Order> implements OrderRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public OrderRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Order> findAllByUserId(long userId, int page, int size) {
        try (Session session = sessionFactory.openSession()) {
            Query<Order> query = session.createQuery("From Order where userId = :userId", Order.class);
            query.setParameter("userId", userId);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.list();
        }
    }

    @Override
    public Order findByUserAndOrderIds(long userId, long orderId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Order> query = session.createQuery("from Order where id = :orderId and userId = :userId",
                                                            Order.class);
            query.setParameter("orderId", orderId);
            query.setParameter("userId", userId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new DaoWrongOrderIdForUserException(e, orderId, userId);
        }
    }

    @Override
    public Order create(Order order) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            GiftCertificatePrice certificate = session.get(GiftCertificatePrice.class, order.getGiftCertificateId());
            order.setCost(certificate.getPrice());
            session.persist(order);
            transaction.commit();
        }
        return order;
    }

    @Override
    public Order findById(long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(Order.class, id);
        }
    }
}
