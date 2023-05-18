package com.epam.esm.service;

import com.epam.esm.model.Tag;
import com.epam.esm.util.RequestParametersHolder;

import java.util.List;

public interface JpaTagService extends JpaService<Tag> {
    Tag findMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(long userId);
    List<Tag> findGiftCertificateTags(long giftCertificateId, RequestParametersHolder rph);
}
