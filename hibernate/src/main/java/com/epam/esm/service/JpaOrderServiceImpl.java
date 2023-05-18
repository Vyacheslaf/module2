package com.epam.esm.service;

import com.epam.esm.model.Order;
import com.epam.esm.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class JpaOrderServiceImpl extends JpaAbstractService<Order> implements JpaOrderService {
    private static final String TIMEZONE = "UTC";

    private OrderRepository repository;

    @Autowired
    public JpaOrderServiceImpl(OrderRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public List<Order> findAllByUserId(long userId, int page, int size) {
        return repository.findAllByUserId(userId, page, size);
    }

    @Override
    public Order findByUserAndOrderIds(long userId, long orderId) {
        return repository.findByUserAndOrderIds(userId, orderId);
    }

    @Override
    public Order create(Order order) {
        order.setPurchaseDate(LocalDateTime.now(ZoneId.of(TIMEZONE)));
        return repository.create(order);
    }
}
