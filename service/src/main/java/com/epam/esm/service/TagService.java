package com.epam.esm.service;

import com.epam.esm.entity.Tag;
import com.epam.esm.exception.dao.DaoException;
import com.epam.esm.exception.service.ServiceException;
import com.epam.esm.util.RequestParametersHolder;

import java.util.List;

public interface TagService extends Service<Tag> {
    Tag findMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(long userId) throws ServiceException, DaoException;
    List<Tag> findGiftCertificateTags(long giftCertificateId,
                                      RequestParametersHolder rph) throws ServiceException, DaoException;
}

