package com.epam.esm.repository;

import com.epam.esm.exception.dao.DaoDuplicateKeyException;
import com.epam.esm.exception.dao.DaoTagForUserNotFoundException;
import com.epam.esm.exception.dao.DaoWrongIdException;
import com.epam.esm.model.*;
import com.epam.esm.model.Order;
import com.epam.esm.util.RequestParametersHolder;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TagRepositoryImpl extends AbstractRepository<Tag> implements TagRepository {
    private static final String TAG_RESOURCE_NAME = "Tag";
    private static final String GIFT_CERTIFICATE_RESOURCE_NAME = "GiftCertificate";

    private final SessionFactory sessionFactory;

    @Autowired
    public TagRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Tag create(Tag tag) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(tag);
            transaction.commit();
        } catch (ConstraintViolationException e) {
            throw new DaoDuplicateKeyException(e, tag.getName());
        }
        return tag;
    }

    @Override
    public Tag findById(long id) {
        try (Session session = sessionFactory.openSession()) {
            Tag tag = session.get(Tag.class, id);
            if (tag == null) {
                throw new DaoWrongIdException(id, TAG_RESOURCE_NAME);
            }
            return tag;
        }
    }

    @Override
    public List<Tag> findAll(RequestParametersHolder rph) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Tag> criteriaQuery = criteriaBuilder.createQuery(Tag.class);
            Root<Tag> root = criteriaQuery.from(Tag.class);
            CriteriaQuery<Tag> select = criteriaQuery.select(root);
            TypedQuery<Tag> typedQuery = session.createQuery(select);
            typedQuery.setFirstResult(rph.getOffset());
            typedQuery.setMaxResults(rph.getSize());
            return typedQuery.getResultList();
        }
    }

    @Override
    public void delete(long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Tag tag = session.get(Tag.class, id);
            session.remove(tag);
            transaction.commit();
        } catch (IllegalArgumentException e) {
            throw new DaoWrongIdException(e, id, TAG_RESOURCE_NAME);
        }
    }

    @Override
    public Tag findMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(long userId) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Tag> cq = cb.createQuery(Tag.class);
            Root<Order> root = cq.from(Order.class);

            Join<Order, GiftCertificate> orderGiftCertificateJoin = root.join(Order_.giftCertificate);
            SetJoin<GiftCertificate, Tag> giftCertificateTagJoin = orderGiftCertificateJoin.join(GiftCertificate_.tags);
            cq.where(cb.equal(root.get(Order_.USER_ID), userId));
            cq.groupBy(giftCertificateTagJoin.get(Tag_.ID));
            cq.orderBy(cb.desc(cb.sum(root.get(Order_.COST))));

            TypedQuery<Tag> query = session.createQuery(cq.select(giftCertificateTagJoin));
            query.setMaxResults(1);
            Tag tag = query.getSingleResult();

            if (tag == null) {
                throw new DaoTagForUserNotFoundException(userId);
            }
            return tag;
        }
    }

    @Override
    public List<Tag> findGiftCertificateTags(long giftCertificateId, RequestParametersHolder rph) {
        try (Session session = sessionFactory.openSession()) {
            GiftCertificate giftCertificate = session.get(GiftCertificate.class, giftCertificateId);
            if (giftCertificate == null) {
                throw new DaoWrongIdException(giftCertificateId, GIFT_CERTIFICATE_RESOURCE_NAME);
            }

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Tag> criteriaQuery = criteriaBuilder.createQuery(Tag.class);

            Root<GiftCertificate> root = criteriaQuery.from(GiftCertificate.class);
            SetJoin<GiftCertificate, Tag> certificates = root.join(GiftCertificate_.tags);
            criteriaQuery.where(criteriaBuilder.equal(root.get(GiftCertificate_.ID), giftCertificateId));
            CriteriaQuery<Tag> select = criteriaQuery.select(certificates);

            TypedQuery<Tag> query = session.createQuery(select);
            query.setFirstResult(rph.getOffset());
            query.setMaxResults(rph.getSize());
            return query.getResultList();
        }
    }
}
