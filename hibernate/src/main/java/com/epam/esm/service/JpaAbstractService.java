package com.epam.esm.service;

import com.epam.esm.repository.Repository;
import com.epam.esm.util.RequestParametersHolder;

import java.util.List;

public class JpaAbstractService<T> implements JpaService<T> {
    private final Repository<T> repository;

    public JpaAbstractService(Repository<T> repository) {
        this.repository = repository;
    }

    @Override
    public T create(T entity) {
        return repository.create(entity);
    }

    @Override
    public T findById(long id) {
        return repository.findById(id);
    }

    @Override
    public List<T> findAll(RequestParametersHolder rph) {
        return repository.findAll(rph);
    }

    @Override
    public T update(T entity) {
        return repository.update(entity);
    }

    @Override
    public void delete(long id) {
        repository.delete(id);
    }
}
