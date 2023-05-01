package com.epam.esm.service;

import com.epam.esm.dao.Dao;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.service.ServiceException;
import com.epam.esm.exception.service.ServiceUnsupportedOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

@Service
@RequestScope
public class TagService extends AbstractService<Tag> {
    @Autowired
    public TagService(Dao<Tag> tagDao) {
        super(tagDao);
    }

    @Override
    public Tag update(Tag entity) throws ServiceException {
        throw new ServiceUnsupportedOperationException();
    }
}
