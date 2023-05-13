package com.epam.esm.service;

import com.epam.esm.entity.Order;

import java.util.List;

public interface OrderService extends Service<Order> {
    List<Order> findAllByUserId(long userId, int page, int size);
    Order findByUserAndOrderIds(long userId, long orderId);
}
