package com.epam.esm.service;

import com.epam.esm.dao.Dao;
import com.epam.esm.dao.OrderDao;
import com.epam.esm.entity.Order;
import com.epam.esm.exception.dao.DaoException;
import com.epam.esm.exception.service.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequestScope
public class OrderServiceImpl extends AbstractService<Order> implements OrderService {
    private final LocalDateTime currentDateTime;
    private static final String UTC_TIMEZONE = "UTC";
    private OrderDao dao;
    @Autowired
    public OrderServiceImpl(OrderDao dao) {
        super(dao);
        this.dao = dao;
        this.currentDateTime = LocalDateTime.now(ZoneId.of(UTC_TIMEZONE));
    }

    @Override
    public List<Order> findAllByUserId(long userId, int page, int size) throws ServiceException, DaoException {
        return dao.findAllByUserId(userId, page, size);
    }

    @Override
    public Order findByUserAndOrderIds(long userId, long orderId) throws ServiceException, DaoException {
        return dao.findByUserAndOrderIds(userId, orderId);
    }

    @Override
    public Order create(Order order) throws ServiceException, DaoException {
        order.setPurchaseDate(currentDateTime);
        return dao.create(order);
    }
}
