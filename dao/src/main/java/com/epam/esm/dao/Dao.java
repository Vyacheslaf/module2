package com.epam.esm.dao;

import com.epam.esm.exception.dao.DaoException;
import com.epam.esm.util.RequestParametersHolder;

import java.util.List;

public interface Dao<T> {
    T create(T entity);
    T findById(long id);
    List<T> findAll(RequestParametersHolder parametersHolder);
    T update(T entity);
    void delete(long id);
}
