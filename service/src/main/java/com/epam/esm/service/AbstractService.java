package com.epam.esm.service;

import com.epam.esm.dao.Dao;
import com.epam.esm.exception.dao.DaoException;
import com.epam.esm.exception.service.ServiceException;
import com.epam.esm.util.RequestParametersHolder;
import lombok.Getter;

import java.util.List;

@Getter
public class AbstractService<T> implements Service<T> {
    private final Dao<T> dao;

    public AbstractService(Dao<T> dao) {
        this.dao = dao;
    }

    @Override
    public T create(T entity) throws ServiceException, DaoException {
        return dao.create(entity);
    }

    @Override
    public T findById(long id) throws ServiceException, DaoException {
        return dao.findById(id);
    }

    @Override
    public List<T> findAll(RequestParametersHolder rph) throws ServiceException, DaoException {
        return dao.findAll(rph);
    }

    @Override
    public T update(T entity) throws ServiceException, DaoException {
        return dao.update(entity);
    }

    @Override
    public void delete(long id) throws ServiceException, DaoException {
        dao.delete(id);
    }
}
