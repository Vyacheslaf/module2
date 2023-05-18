package com.epam.esm.service;

import com.epam.esm.model.Tag;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.util.RequestParametersHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JpaTagServiceImpl extends JpaAbstractService<Tag> implements JpaTagService {
    private final TagRepository repository;
    @Autowired
    public JpaTagServiceImpl(TagRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Tag findMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(long userId) {
        return repository.findMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(userId);
    }

    @Override
    public List<Tag> findGiftCertificateTags(long giftCertificateId, RequestParametersHolder rph) {
        return repository.findGiftCertificateTags(giftCertificateId, rph);
    }
}
