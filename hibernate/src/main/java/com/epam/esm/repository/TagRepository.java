package com.epam.esm.repository;

import com.epam.esm.model.Tag;
import com.epam.esm.util.RequestParametersHolder;

import java.util.List;

public interface TagRepository extends Repository<Tag> {
    Tag findMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(long userId);
    List<Tag> findGiftCertificateTags(long giftCertificateId, RequestParametersHolder rph);
}
