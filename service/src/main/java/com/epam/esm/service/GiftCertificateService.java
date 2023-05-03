package com.epam.esm.service;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.exception.dao.DaoException;
import com.epam.esm.exception.service.ServiceException;

public interface GiftCertificateService extends Service<GiftCertificate> {
    GiftCertificate updateDuration(long id, int duration) throws ServiceException, DaoException;
}
