package com.epam.esm.service;

import com.epam.esm.model.Tag;
import com.epam.esm.exception.service.ServiceWrongTagNameException;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.repository.GiftCertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
public class JpaGiftCertificateServiceImpl
        extends JpaAbstractService<GiftCertificate> implements JpaGiftCertificateService {
    private static final String TIMEZONE = "UTC";

    private final GiftCertificateRepository repository;

    @Autowired
    public JpaGiftCertificateServiceImpl(GiftCertificateRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public GiftCertificate create(GiftCertificate certificate) {
        LocalDateTime currentDateTime = LocalDateTime.now(ZoneId.of(TIMEZONE));
        if (certificate.getCreateDate() == null) {
            certificate.setCreateDate(currentDateTime);
        }
        if (certificate.getLastUpdateDate() == null) {
            certificate.setLastUpdateDate(currentDateTime);
        }
        checkTags(certificate);
        return repository.create(certificate);
    }

    @Override
    public GiftCertificate update(GiftCertificate certificate) {
        if (certificate.getLastUpdateDate() == null) {
            certificate.setLastUpdateDate(LocalDateTime.now(ZoneId.of(TIMEZONE)));
        }
        checkTags(certificate);
        return repository.update(certificate);
    }

    @Override
    public GiftCertificate updateDuration(long id, int duration) {
        return repository.updateDuration(id, duration, LocalDateTime.now(ZoneId.of(TIMEZONE)));
    }

    private void checkTags(GiftCertificate certificate) {
        if (certificate.getTags() == null) {
            certificate.setTags(new HashSet<>());
        }
        Set<Tag> tags = certificate.getTags();
        if (!tags.isEmpty()
                && (tags.contains(null) || (tags.stream().map(Tag::getName).filter(Objects::isNull).count() != 0))) {
            throw new ServiceWrongTagNameException();
        }
    }
}
