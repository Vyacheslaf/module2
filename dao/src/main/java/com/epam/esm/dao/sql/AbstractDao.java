package com.epam.esm.dao.sql;

import com.epam.esm.dao.Dao;
import com.epam.esm.exception.dao.DaoUnsupportedOperationException;
import com.epam.esm.util.RequestParametersHolder;

import java.util.List;

public abstract class AbstractDao<T> implements Dao<T> {
    @Override
    public T create(T entity) {
        throw new DaoUnsupportedOperationException();
    }

    @Override
    public T findById(long id) {
        throw new DaoUnsupportedOperationException();
    }

    @Override
    public List<T> findAll(RequestParametersHolder parametersHolder) {
        throw new DaoUnsupportedOperationException();
    }

    @Override
    public T update(T entity) {
        throw new DaoUnsupportedOperationException();
    }

    @Override
    public void delete(long id) {
        throw new DaoUnsupportedOperationException();
    }
}
