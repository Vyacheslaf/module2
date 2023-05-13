package com.epam.esm.dao;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.exception.dao.DaoException;

import java.time.LocalDateTime;

public interface GiftCertificateDao extends Dao<GiftCertificate> {
    GiftCertificate updateDuration(long id, int duration, LocalDateTime ldt);
}
