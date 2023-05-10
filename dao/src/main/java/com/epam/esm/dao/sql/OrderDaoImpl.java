package com.epam.esm.dao.sql;

import com.epam.esm.dao.OrderDao;
import com.epam.esm.entity.Order;
import com.epam.esm.exception.dao.DaoException;
import com.epam.esm.exception.dao.DaoWrongIdException;
import com.epam.esm.exception.dao.DaoWrongOrderIdForUserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.annotation.RequestScope;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.NoSuchElementException;

@Repository
@RequestScope
public class OrderDaoImpl extends AbstractDao<Order> implements OrderDao {
    private static final String RESOURCE_NAME = "Order";
    private static final String FIND_ALL_USER_ORDERS_QUERY = "SELECT * FROM `order` WHERE user_id = ? LIMIT ? OFFSET ?";
    private static final String FIND_BY_USER_AND_ORDER_IDS = "SELECT * FROM `order` WHERE id = ? AND user_id = ?";
    private static final String CREATE_QUERY = "INSERT INTO `order` VALUES (DEFAULT, ?, ?, " +
                                                                "(SELECT price FROM gift_certificate WHERE id = ?), ?)";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM `order` WHERE id = ?";
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OrderDaoImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Order> findAllByUserId(long userId, int page, int size) throws DaoException {
        return jdbcTemplate.query(FIND_ALL_USER_ORDERS_QUERY, new BeanPropertyRowMapper<>(Order.class),
                            userId, size, page * size);
    }

    @Override
    public Order findByUserAndOrderIds(long userId, long orderId) throws DaoException {
        try {
            return jdbcTemplate.query(FIND_BY_USER_AND_ORDER_IDS, new BeanPropertyRowMapper<>(Order.class), orderId,
                                      userId)
                    .stream().findAny().orElseThrow();
        } catch (NoSuchElementException e) {
            throw new DaoWrongOrderIdForUserException(e, orderId, userId);
        }
    }

    @Override
    public Order create(Order order) throws DaoException {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement pstmt = connection.prepareStatement(CREATE_QUERY, Statement.RETURN_GENERATED_KEYS);
            int k = 0;
            pstmt.setLong(++k, order.getUserId());
            pstmt.setLong(++k, order.getGiftCertificateId());
            pstmt.setLong(++k, order.getGiftCertificateId());
            pstmt.setTimestamp(++k, Timestamp.valueOf(order.getPurchaseDate()));
            return pstmt;
            }, keyHolder);
        return findById(keyHolder.getKey().longValue());
    }

    @Override
    public Order findById(long id) throws DaoException {
        try {
            return jdbcTemplate.query(FIND_BY_ID_QUERY, new BeanPropertyRowMapper<>(Order.class), id)
                    .stream().findAny().orElseThrow();
        } catch (NoSuchElementException e) {
            throw new DaoWrongIdException(e, id, RESOURCE_NAME);
        }
    }
}
