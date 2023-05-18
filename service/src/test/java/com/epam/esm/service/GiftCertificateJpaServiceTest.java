package com.epam.esm.service;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.service.ServiceWrongTagNameException;
import com.epam.esm.util.RequestParametersHolder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GiftCertificateJpaServiceTest {
    private static final int MOCK_CERTIFICATE_LIST_SIZE = 3;
    private static List<GiftCertificate> giftCertificates;
    private static RequestParametersHolder rph;
    @Mock
    private static GiftCertificateDao mockDao;
    @InjectMocks
    private GiftCertificateServiceImpl giftCertificateService;
    @Captor
    ArgumentCaptor<LocalDateTime> ldtCaptor;

    @BeforeAll
    public static void prepareMockDao() {
        giftCertificates = new ArrayList<>();
        for (int i = 1; i < MOCK_CERTIFICATE_LIST_SIZE + 1; i++) {
            GiftCertificate certificate = new GiftCertificate();
            certificate.setName("certificate" + i);
            certificate.setId(i);
            giftCertificates.add(certificate);
        }
        rph = new RequestParametersHolder(0, 5);
    }

    @ParameterizedTest
    @CsvSource(value = {"NULL, NULL",
            "NULL, 2001-01-01T01:01:01.001",
            "2001-01-01T01:01:01.001, NULL",
            "2001-01-01T01:01:01.001, 2001-01-01T01:01:01.001"},
            nullValues = {"NULL"})
    public void createTest(String createDate, String updateDate) {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setName("certificate1");
        certificate.setCreateDate(getDate(createDate));
        certificate.setLastUpdateDate(getDate(updateDate));
        when(mockDao.create(certificate)).thenReturn(giftCertificates.get(0));

        assertEquals(giftCertificates.get(0), giftCertificateService.create(certificate));
    }

    private LocalDateTime getDate(String date) {
        if ((date == null) || date.equalsIgnoreCase("")) {
            return null;
        }
        return LocalDateTime.parse(date);
    }

    @Test
    public void findByIdTest() {
        int id = 2;
        when(mockDao.findById(id)).thenReturn(giftCertificates.get(id - 1));

        assertEquals(giftCertificates.get(id - 1), giftCertificateService.findById(id));
    }

    @Test
    public void findAllTest() {
        when(mockDao.findAll(any(RequestParametersHolder.class))).thenReturn(giftCertificates);

        assertEquals(MOCK_CERTIFICATE_LIST_SIZE, giftCertificateService.findAll(rph).size());
    }

    @Test
    public void deleteTest() {
        int id = 3;
        giftCertificateService.delete(id);
        verify(mockDao, atLeastOnce()).delete(anyLong());
    }

    @ParameterizedTest
    @CsvSource(value = {"NULL",
                        "2001-01-01T01:01:01.001"},
            nullValues = {"NULL"})
    public void updateTest(String updateDate) {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setName("certificate1");
        certificate.setLastUpdateDate(getDate(updateDate));
        when(mockDao.update(certificate)).thenReturn(giftCertificates.get(0));

        assertEquals(giftCertificates.get(0).getId(), giftCertificateService.update(certificate).getId());
    }

    @Test
    public void updateDurationTest() {
        giftCertificateService.updateDuration(0, 0);
        verify(mockDao).updateDuration(anyLong(), anyInt(), ldtCaptor.capture());
        LocalDateTime localDateTime = ldtCaptor.getValue();

        assertEquals(LocalDateTime.now(ZoneId.of("UTC")).getYear(), localDateTime.getYear());
    }

    @Test
    public void wrongTagNameExceptionTest() {
        Set<Tag> tags = new HashSet<>();
        tags.add(new Tag());
        GiftCertificate giftCertificate = new GiftCertificate();
        giftCertificate.setTags(tags);

        assertThrows(ServiceWrongTagNameException.class,
                    () -> new GiftCertificateServiceImpl(mockDao).create(giftCertificate));

        tags = new HashSet<>();
        tags.add(null);
        giftCertificate.setTags(tags);

        assertThrows(ServiceWrongTagNameException.class,
                () -> new GiftCertificateServiceImpl(mockDao).create(giftCertificate));
    }
}
