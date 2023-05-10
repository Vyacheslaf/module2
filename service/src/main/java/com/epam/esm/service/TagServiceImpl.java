package com.epam.esm.service;

import com.epam.esm.dao.Dao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.dao.DaoException;
import com.epam.esm.exception.service.ServiceException;
import com.epam.esm.exception.service.ServiceUnsupportedOperationException;
import com.epam.esm.util.RequestParametersHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;

@Service
@RequestScope
public class TagServiceImpl extends AbstractService<Tag> implements TagService{
    private final TagDao dao;
    @Autowired
    public TagServiceImpl(TagDao dao) {
        super(dao);
        this.dao = dao;
    }

    @Override
    public Tag update(Tag entity) throws ServiceException {
        throw new ServiceUnsupportedOperationException();
    }

    @Override
    public Tag findMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(long userId) throws DaoException {
        return dao.findMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(userId);
    }

    @Override
    public List<Tag> findGiftCertificateTags(long giftCertificateId,
                                             RequestParametersHolder rph) throws DaoException {
        return dao.findGiftCertificateTags(giftCertificateId, rph);
    }
}
