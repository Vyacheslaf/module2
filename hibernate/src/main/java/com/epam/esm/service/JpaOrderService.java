package com.epam.esm.service;

import com.epam.esm.model.Order;

import java.util.List;

public interface JpaOrderService extends JpaService<Order> {
    List<Order> findAllByUserId(long userId, int page, int size);
    Order findByUserAndOrderIds(long userId, long orderId);
}
