package com.epam.esm.exception.dao;

import lombok.Getter;

@Getter
public class DaoWrongIdException extends DaoException {
    private long id;
    private String resourceName;

    public DaoWrongIdException(Throwable cause, long id, String resourceName) {
        super(cause);
        this.id = id;
        this.resourceName = resourceName;
    }

    public DaoWrongIdException(long id, String resourceName) {
        super();
        this.id = id;
        this.resourceName = resourceName;
    }
}
