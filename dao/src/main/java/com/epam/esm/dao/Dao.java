package com.epam.esm.dao;

import com.epam.esm.exception.dao.DaoException;
import com.epam.esm.util.RequestParametersHolder;

import java.util.List;

public interface Dao<T> {
    T create(T entity) throws DaoException;
    T findById(long id) throws DaoException;
    List<T> findAll(RequestParametersHolder parametersHolder) throws DaoException;
    T update(T entity) throws DaoException;
    void delete(long id) throws DaoException;
}
