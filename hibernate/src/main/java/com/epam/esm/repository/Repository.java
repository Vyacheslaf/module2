package com.epam.esm.repository;

import com.epam.esm.util.RequestParametersHolder;

import java.util.List;

public interface Repository<T> {
    T create(T entity);
    T findById(long id);
    List<T> findAll(RequestParametersHolder parametersHolder);
    T update(T entity);
    void delete(long id);
}
