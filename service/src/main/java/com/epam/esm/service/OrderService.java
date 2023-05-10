package com.epam.esm.service;

import com.epam.esm.entity.Order;
import com.epam.esm.exception.dao.DaoException;
import com.epam.esm.exception.service.ServiceException;

import java.util.List;

public interface OrderService extends Service<Order> {
    List<Order> findAllByUserId(long userId, int page, int size) throws ServiceException, DaoException;
    Order findByUserAndOrderIds(long userId, long orderId) throws ServiceException, DaoException;
}
