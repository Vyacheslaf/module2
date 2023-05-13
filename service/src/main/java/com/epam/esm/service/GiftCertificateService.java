package com.epam.esm.service;

import com.epam.esm.entity.GiftCertificate;

public interface GiftCertificateService extends Service<GiftCertificate> {
    GiftCertificate updateDuration(long id, int duration);
}
