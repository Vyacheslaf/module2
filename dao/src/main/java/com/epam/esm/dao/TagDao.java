package com.epam.esm.dao;

import com.epam.esm.entity.Tag;
import com.epam.esm.exception.dao.DaoException;
import com.epam.esm.util.RequestParametersHolder;

import java.util.List;

public interface TagDao extends Dao<Tag> {
    Tag findMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(long userId);
    List<Tag> findGiftCertificateTags(long giftCertificateId, RequestParametersHolder rph);
}
