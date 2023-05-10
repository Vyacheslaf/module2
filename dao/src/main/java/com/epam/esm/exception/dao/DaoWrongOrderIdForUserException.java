package com.epam.esm.exception.dao;

import lombok.Getter;

@Getter
public class DaoWrongOrderIdForUserException extends DaoException {
    private long orderId;
    private long userId;

    public DaoWrongOrderIdForUserException(Throwable cause, long orderId, long userId) {
        super(cause);
        this.orderId = orderId;
        this.userId = userId;
    }

    public DaoWrongOrderIdForUserException(long orderId, long userId) {
        super();
        this.orderId = orderId;
        this.userId = userId;
    }
}
