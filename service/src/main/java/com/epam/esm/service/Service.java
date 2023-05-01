package com.epam.esm.service;

import com.epam.esm.exception.dao.DaoException;
import com.epam.esm.exception.service.ServiceException;
import com.epam.esm.util.RequestParametersHolder;

import java.util.List;

public interface Service<T> {
    T create(T entity) throws ServiceException, DaoException;
    T findById(long id) throws ServiceException, DaoException;
    List<T> findAll(RequestParametersHolder rph) throws ServiceException, DaoException;
    T update(T entity) throws ServiceException, DaoException;
    void delete(long id) throws ServiceException, DaoException;
}
