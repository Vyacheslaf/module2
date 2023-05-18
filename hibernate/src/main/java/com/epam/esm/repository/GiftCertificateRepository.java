package com.epam.esm.repository;

import com.epam.esm.model.GiftCertificate;

import java.time.LocalDateTime;

public interface GiftCertificateRepository extends Repository<GiftCertificate> {
    GiftCertificate updateDuration(long id, int duration, LocalDateTime ldt);
}
