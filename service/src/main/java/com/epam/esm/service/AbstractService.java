package com.epam.esm.service;

import com.epam.esm.dao.Dao;
import com.epam.esm.util.RequestParametersHolder;

import java.util.List;

public class AbstractService<T> implements Service<T> {
    private final Dao<T> dao;

    public AbstractService(Dao<T> dao) {
        this.dao = dao;
    }

    @Override
    public T create(T entity) {
        return dao.create(entity);
    }

    @Override
    public T findById(long id) {
        return dao.findById(id);
    }

    @Override
    public List<T> findAll(RequestParametersHolder rph) {
        return dao.findAll(rph);
    }

    @Override
    public T update(T entity) {
        return dao.update(entity);
    }

    @Override
    public void delete(long id) {
        dao.delete(id);
    }
}
