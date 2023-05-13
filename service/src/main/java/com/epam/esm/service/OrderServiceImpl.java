package com.epam.esm.service;

import com.epam.esm.dao.OrderDao;
import com.epam.esm.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class OrderServiceImpl extends AbstractService<Order> implements OrderService {
    private static final String TIMEZONE = "UTC";
    private final OrderDao dao;
    @Autowired
    public OrderServiceImpl(OrderDao dao) {
        super(dao);
        this.dao = dao;
    }

    @Override
    public List<Order> findAllByUserId(long userId, int page, int size) {
        return dao.findAllByUserId(userId, page, size);
    }

    @Override
    public Order findByUserAndOrderIds(long userId, long orderId) {
        return dao.findByUserAndOrderIds(userId, orderId);
    }

    @Override
    public Order create(Order order) {
        order.setPurchaseDate(LocalDateTime.now(ZoneId.of(TIMEZONE)));
        return dao.create(order);
    }
}
