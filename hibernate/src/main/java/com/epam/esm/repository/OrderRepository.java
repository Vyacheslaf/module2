package com.epam.esm.repository;

import com.epam.esm.model.Order;

import java.util.List;

public interface OrderRepository extends Repository<Order> {
    List<Order> findAllByUserId(long userId, int page, int size);
    Order findByUserAndOrderIds(long userId, long orderId);
}
