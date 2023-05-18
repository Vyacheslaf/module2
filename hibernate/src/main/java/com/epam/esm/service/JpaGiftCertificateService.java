package com.epam.esm.service;

import com.epam.esm.model.GiftCertificate;

public interface JpaGiftCertificateService extends JpaService<GiftCertificate> {
    GiftCertificate updateDuration(long id, int duration);
}
