package com.epam.esm.service;

import com.epam.esm.dao.Dao;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.dao.DaoException;
import com.epam.esm.exception.service.ServiceWrongTagNameException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Set;

@Service
@RequestScope
public class GiftCertificateService extends AbstractService<GiftCertificate> {
    private final LocalDateTime currentDateTime;
    private static final String UTC_TIMEZONE = "UTC";

    @Autowired
    public GiftCertificateService(Dao<GiftCertificate> dao) {
        super(dao);
        this.currentDateTime = LocalDateTime.now(ZoneId.of(UTC_TIMEZONE));
    }

    @Override
    public GiftCertificate create(GiftCertificate certificate) throws ServiceWrongTagNameException, DaoException {
        if (certificate.getCreateDate() == null) {
            certificate.setCreateDate(currentDateTime);
        }
        if (certificate.getLastUpdateDate() == null) {
            certificate.setLastUpdateDate(currentDateTime);
        }
        checkTags(certificate);
        return super.getDao().create(certificate);
    }

    @Override
    public GiftCertificate update(GiftCertificate certificate) throws DaoException, ServiceWrongTagNameException {
        if (certificate.getLastUpdateDate() == null) {
            certificate.setLastUpdateDate(currentDateTime);
        }
        checkTags(certificate);
        return super.getDao().update(certificate);
    }

    private void checkTags(GiftCertificate certificate) throws ServiceWrongTagNameException {
        Set<Tag> tags = certificate.getTags();
        if (!certificate.getTags().isEmpty() && tags.stream().map(Tag::getName).filter(Objects::isNull).count() != 0) {
            throw new ServiceWrongTagNameException();
        }
    }
}
