package com.epam.esm.exception.dao;

import lombok.Getter;

@Getter
public class DaoWrongOrderFieldsException extends DaoException {
    private long userId;
    private long giftCertificateId;
    public DaoWrongOrderFieldsException(Exception e, long userId, long giftCertificateId) {
        this.userId = userId;
        this.giftCertificateId = giftCertificateId;
    }
}
