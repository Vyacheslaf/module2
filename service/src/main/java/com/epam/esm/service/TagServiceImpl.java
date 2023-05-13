package com.epam.esm.service;

import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Tag;
import com.epam.esm.util.RequestParametersHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl extends AbstractService<Tag> implements TagService {
    private final TagDao dao;
    @Autowired
    public TagServiceImpl(TagDao dao) {
        super(dao);
        this.dao = dao;
    }

    @Override
    public Tag findMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(long userId) {
        return dao.findMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(userId);
    }

    @Override
    public List<Tag> findGiftCertificateTags(long giftCertificateId, RequestParametersHolder rph) {
        return dao.findGiftCertificateTags(giftCertificateId, rph);
    }
}
