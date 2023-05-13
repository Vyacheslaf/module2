package com.epam.esm.service;

import com.epam.esm.entity.Tag;
import com.epam.esm.util.RequestParametersHolder;

import java.util.List;

public interface TagService extends Service<Tag> {
    Tag findMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(long userId);
    List<Tag> findGiftCertificateTags(long giftCertificateId, RequestParametersHolder rph);
}

