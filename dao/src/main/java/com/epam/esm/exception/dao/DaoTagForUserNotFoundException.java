package com.epam.esm.exception.dao;

import lombok.Getter;

@Getter
public class DaoTagForUserNotFoundException extends DaoException {
    private long userId;

    public DaoTagForUserNotFoundException(Throwable cause, long userId) {
        super(cause);
        this.userId = userId;
    }

    public DaoTagForUserNotFoundException(long userId) {
        super();
        this.userId = userId;
    }
}
