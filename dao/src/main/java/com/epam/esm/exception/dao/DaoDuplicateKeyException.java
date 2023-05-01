package com.epam.esm.exception.dao;

import lombok.Getter;

@Getter
public class DaoDuplicateKeyException extends DaoException {
    private String keyName;

    public DaoDuplicateKeyException(Throwable cause) {
        super(cause);
    }

    public DaoDuplicateKeyException(Throwable cause, String keyName) {
        super(cause);
        this.keyName = keyName;
    }
}
