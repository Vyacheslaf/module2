package com.epam.esm.repository;

import com.epam.esm.exception.InvalidSortRequestException;
import com.epam.esm.exception.InvalidTagNameException;
import com.epam.esm.exception.dao.DaoWrongIdException;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.GiftCertificate_;
import com.epam.esm.model.Tag;
import com.epam.esm.model.Tag_;
import com.epam.esm.util.RequestParametersHolder;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public class GiftCertificateRepositoryImpl
        extends AbstractRepository<GiftCertificate> implements GiftCertificateRepository {
    private static final String RESOURCE_NAME = "GiftCertificate";
    private static final String SORT_PATTERN = "^((name|createDate|lastUpdateDate).(asc|desc)){1}$";
    private static final String SORT_DIRECTION_ASC = "asc";
    private static final String SORT_DIRECTION_DESC = "desc";
    private static final String SORT_REPLACE_PATTERN = "\\.";
    private static final String SQL_ANY_SYMBOL = "%";
    private final SessionFactory sessionFactory;

    @Autowired
    public GiftCertificateRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public GiftCertificate create(GiftCertificate certificate) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            if (certificate.getTags() != null) {
                createOrReferenceTags(certificate, session);
            }
            session.persist(certificate);
            transaction.commit();
        }
        return certificate;
    }

    private void createOrReferenceTags(GiftCertificate certificate, Session session) {
        for (Tag tag : certificate.getTags()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Tag> cq = cb.createQuery(Tag.class);
            Root<Tag> root = cq.from(Tag.class);
            cq.where(cb.equal(root.get(Tag_.NAME), tag.getName()));

            TypedQuery<Tag> query = session.createQuery(cq.select(root));
            Tag dbTag;
            try {
                dbTag = query.getSingleResult();
                tag.setId(dbTag.getId());
            } catch (NoResultException e) {
                session.persist(tag);
            }
        }
    }

    @Override
    public List<GiftCertificate> findAll(RequestParametersHolder rph) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<GiftCertificate> cq = cb.createQuery(GiftCertificate.class);
            Root<GiftCertificate> root = cq.from(GiftCertificate.class);
            CriteriaQuery<GiftCertificate> select = cq.select(root);

            if ((rph.getSortList() != null) && !rph.getSortList().isEmpty()) {
                cq.orderBy(getCriteriaOrders(rph, cb, root));
            }

            List<Predicate> predicates = new ArrayList<>();

            if ((rph.getTags() != null) && !rph.getTags().isEmpty()) {
                for (String tagName : rph.getTags()) {
                    if ((tagName == null) || tagName.isEmpty()) {
                        throw new InvalidTagNameException();
                    }

                    Subquery<Long> subquery = cq.subquery(Long.class);
                    Root<GiftCertificate> subqueryRoot = subquery.from(GiftCertificate.class);
                    Join<GiftCertificate, Tag> tagGiftCertificateJoin = subqueryRoot.join(GiftCertificate_.TAGS);

                    subquery.select(subqueryRoot.get(GiftCertificate_.ID))
                            .where(cb.equal(tagGiftCertificateJoin.get(Tag_.NAME), tagName));

                    predicates.add(cb.in(root.get(GiftCertificate_.ID)).value(subquery));
                }
            }

            String searchPattern = SQL_ANY_SYMBOL + rph.getSearch() + SQL_ANY_SYMBOL;
            if ((rph.getSearch() != null) && !rph.getSearch().isEmpty()) {
                predicates.add(cb.or(
                        cb.like(root.get(GiftCertificate_.NAME), searchPattern),
                        cb.like(root.get(GiftCertificate_.DESCRIPTION), searchPattern)));
            }

            if (!predicates.isEmpty()) {
                select.where(cb.and(predicates.toArray(new Predicate[0])));
            }

            TypedQuery<GiftCertificate> typedQuery = session.createQuery(select);
            typedQuery.setFirstResult(rph.getOffset());
            typedQuery.setMaxResults(rph.getSize());
            return typedQuery.getResultList();
        }
    }
    private Order[] getCriteriaOrders(RequestParametersHolder rph,
                                   CriteriaBuilder criteriaBuilder,
                                   Root<GiftCertificate> root) {
        Order[] orders = new Order[rph.getSortList().size()];
        int i = 0;
        for (String sort : rph.getSortList()) {
            if ((sort == null) || !sort.matches(SORT_PATTERN)) {
                throw new InvalidSortRequestException(SORT_PATTERN.substring(2, SORT_PATTERN.length() - 5));
            }
            String[] splitSort = sort.split(SORT_REPLACE_PATTERN);
            if (splitSort[1].equalsIgnoreCase(SORT_DIRECTION_ASC)) {
                orders[i++] = criteriaBuilder.asc(root.get(splitSort[0]));
            }
            if (splitSort[1].equalsIgnoreCase(SORT_DIRECTION_DESC)) {
                orders[i++] = criteriaBuilder.desc(root.get(splitSort[0]));
            }
        }
        return orders;
    }

    @Override
    public GiftCertificate findById(long id) {
        try (Session session = sessionFactory.openSession()) {
            GiftCertificate giftCertificate = session.get(GiftCertificate.class, id);
            if (giftCertificate == null) {
                throw new DaoWrongIdException(id, RESOURCE_NAME);
            }
            return giftCertificate;
        }
    }

    @Override
    public GiftCertificate update(GiftCertificate certificate) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            GiftCertificate dbCertificate = session.find(GiftCertificate.class, certificate.getId());
            if (dbCertificate == null) {
                throw new DaoWrongIdException(certificate.getId(), RESOURCE_NAME);
            }
            if ((certificate.getName() != null) && !certificate.getName().isEmpty()) {
                dbCertificate.setName(certificate.getName());
            }
            if ((certificate.getDescription() != null) && !certificate.getDescription().isEmpty()) {
                dbCertificate.setDescription(certificate.getDescription());
            }
            if (certificate.getPrice() != null) {
                dbCertificate.setPrice(certificate.getPrice());
            }
            if (certificate.getDuration() != null) {
                dbCertificate.setDuration(certificate.getDuration());
            }
            if (certificate.getCreateDate() != null) {
                dbCertificate.setCreateDate(certificate.getCreateDate());
            }
            if (certificate.getLastUpdateDate() != null) {
                dbCertificate.setLastUpdateDate(certificate.getLastUpdateDate());
            }
            if (certificate.getTags() != null) {
                createOrReferenceTags(certificate, session);
                dbCertificate.setTags(certificate.getTags());
            }
            transaction.commit();
            return dbCertificate;
        }
    }

    @Override
    public GiftCertificate updateDuration(long id, int duration, LocalDateTime ldt) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            GiftCertificate giftCertificate = session.find(GiftCertificate.class, id);
            if (giftCertificate == null) {
                throw new DaoWrongIdException(id, RESOURCE_NAME);
            }
            giftCertificate.setDuration(duration);
            giftCertificate.setLastUpdateDate(ldt);
            transaction.commit();
            return giftCertificate;
        }
    }

    @Override
    public void delete(long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            GiftCertificate certificate = session.get(GiftCertificate.class, id);
            session.remove(certificate);
            transaction.commit();
        } catch (IllegalArgumentException e) {
            throw new DaoWrongIdException(e, id, RESOURCE_NAME);
        }
    }
}
