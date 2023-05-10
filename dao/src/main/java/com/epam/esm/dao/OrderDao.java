package com.epam.esm.dao;

import com.epam.esm.dao.Dao;
import com.epam.esm.entity.Order;
import com.epam.esm.exception.dao.DaoException;

import java.util.List;

public interface OrderDao extends Dao<Order> {
    List<Order> findAllByUserId(long userId, int page, int size) throws DaoException;
    Order findByUserAndOrderIds(long userId, long orderId) throws DaoException;
}
