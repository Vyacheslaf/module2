package com.epam.esm.service;

import com.epam.esm.util.RequestParametersHolder;

import java.util.List;

public interface JpaService<T> {
    T create(T entity);
    T findById(long id);
    List<T> findAll(RequestParametersHolder rph);
    T update(T entity);
    void delete(long id);
}
