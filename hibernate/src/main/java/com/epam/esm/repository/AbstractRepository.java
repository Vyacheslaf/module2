package com.epam.esm.repository;

import com.epam.esm.exception.dao.DaoUnsupportedOperationException;
import com.epam.esm.util.RequestParametersHolder;

import java.util.List;

public abstract class AbstractRepository<T> implements Repository<T> {
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
