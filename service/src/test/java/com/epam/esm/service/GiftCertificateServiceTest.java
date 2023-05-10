package com.epam.esm.service;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.util.GiftCertificateSortMap;
import com.epam.esm.dao.sql.GiftCertificateDaoImpl;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.dao.DaoException;
import com.epam.esm.exception.service.ServiceException;
import com.epam.esm.util.RequestParametersHolder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class GiftCertificateServiceTest {
    private static final int MOCK_CERTIFICATE_LIST_SIZE = 3;
    private static final int H2_CERTIFICATE_LIST_SIZE = 2;
    private static GiftCertificateDao mockDao;
    private static List<GiftCertificate> giftCertificates;
    private DataSource dataSource;
    private Service<GiftCertificate> service;
    private static GiftCertificateSortMap giftCertificateSortMap;

    @BeforeAll
    public static void prepareMockDao() {
        mockDao = Mockito.mock(GiftCertificateDao.class);
        giftCertificates = new ArrayList<>();
        for (int i = 1; i < MOCK_CERTIFICATE_LIST_SIZE + 1; i++) {
            GiftCertificate certificate = new GiftCertificate();
            certificate.setName("certificate" + i);
            certificate.setId(i);
            giftCertificates.add(certificate);
        }
        Map<String, String> map = new HashMap<>();
        map.put("name", "gc.name");
        map.put("date", "gc.create_date");
        giftCertificateSortMap = new GiftCertificateSortMap(map);
    }
    @BeforeEach
    public void prepareDataSource() {
        dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
                                                  .setName("testDB;MODE=MySQL")
                                                  .addScript("test/schema.sql")
                                                  .addScript("test/data.sql")
                                                  .build();
        GiftCertificateDao dao = new GiftCertificateDaoImpl(dataSource, giftCertificateSortMap);
        service = new GiftCertificateServiceImpl(dao);
    }

    @ParameterizedTest
    @CsvSource(value = {"NULL, NULL",
            "NULL, 2001-01-01T01:01:01.001",
            "2001-01-01T01:01:01.001, NULL",
            "2001-01-01T01:01:01.001, 2001-01-01T01:01:01.001"},
            nullValues = {"NULL"})
    public void createTest(String createDate, String updateDate) throws DaoException, ServiceException {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setName("certificate1");
        certificate.setCreateDate(getDate(createDate));
        certificate.setLastUpdateDate(getDate(updateDate));
        when(mockDao.create(certificate)).thenReturn(giftCertificates.get(0));

        GiftCertificateServiceImpl certificateService = new GiftCertificateServiceImpl(mockDao);

        assertEquals(giftCertificates.get(0), certificateService.create(certificate));
    }

    private LocalDateTime getDate(String date) {
        if ((date == null) || date.equalsIgnoreCase("")) {
            return null;
        }
        return LocalDateTime.parse(date);
    }

    @Test
    public void findByIdTest() throws DaoException, ServiceException {
        int id = 2;
        when(mockDao.findById(id)).thenReturn(giftCertificates.get(id - 1));

        GiftCertificateServiceImpl certificateService = new GiftCertificateServiceImpl(mockDao);

        assertEquals(giftCertificates.get(id - 1), certificateService.findById(id));
    }

    @Test
    public void findAllTest() throws DaoException, ServiceException {
        when(mockDao.findAll(any(RequestParametersHolder.class))).thenReturn(giftCertificates);

        GiftCertificateServiceImpl certificateService = new GiftCertificateServiceImpl(mockDao);

        assertEquals(MOCK_CERTIFICATE_LIST_SIZE, certificateService.findAll(new RequestParametersHolder()).size());
    }

    @Test
    public void deleteTest() throws DaoException, ServiceException {
        int id = 3;
        GiftCertificateServiceImpl certificateService = new GiftCertificateServiceImpl(mockDao);
        certificateService.delete(id);
        verify(mockDao, atLeastOnce()).delete(anyLong());
    }

    @ParameterizedTest
    @CsvSource(value = {"NULL",
                        "2001-01-01T01:01:01.001"},
            nullValues = {"NULL"})
    public void updateTest(String updateDate) throws ServiceException, DaoException {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setName("certificate1");
        certificate.setLastUpdateDate(getDate(updateDate));
        when(mockDao.update(certificate)).thenReturn(giftCertificates.get(0));

        GiftCertificateServiceImpl certificateService = new GiftCertificateServiceImpl(mockDao);

        assertEquals(giftCertificates.get(0).getId(), certificateService.update(certificate).getId());
    }

    @Test
    public void h2FindAllTest() throws ServiceException, DaoException {
        assertEquals(H2_CERTIFICATE_LIST_SIZE, service.findAll(new RequestParametersHolder()).size());
    }

    @Test
    public void h2DeleteTest() throws ServiceException, DaoException {
        long id = 1;
        service.delete(id);
        assertEquals(H2_CERTIFICATE_LIST_SIZE - 1, service.findAll(new RequestParametersHolder()).size());
    }

    @Test
    public void h2CreateTest() throws ServiceException, DaoException {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setName("some_name");
        certificate.setDescription("some_description");
        certificate.setPrice(0);
        certificate.setDuration(0);

        long expectedValue = H2_CERTIFICATE_LIST_SIZE + 1;
        assertEquals(expectedValue, service.create(certificate).getId());
        assertEquals(expectedValue, service.findAll(new RequestParametersHolder()).size());

        certificate.getTags().add(new Tag(0, "tag1"));
        certificate.getTags().add(new Tag(0, "tag22"));

        expectedValue++;
        assertEquals(expectedValue, service.create(certificate).getId());
        assertEquals(expectedValue, service.findAll(new RequestParametersHolder()).size());
        int tagsCount = new JdbcTemplate(dataSource).queryForObject("SELECT COUNT(id) FROM tag", Integer.class);
        assertEquals(expectedValue, tagsCount);
    }

    @Test
    public void h2FindByIdTest() throws ServiceException, DaoException {
        long id = 1;
        LocalDateTime ldt = LocalDateTime.parse("2001-01-01T01:01:01.001");
        GiftCertificate expected = new GiftCertificate(id, "name1", "description1",
                                                    111, 11, ldt, ldt, new LinkedHashSet<>());
        expected.getTags().add(new Tag(1, "tag1"));
        expected.getTags().add(new Tag(3, "tag3"));

        assertEquals(expected, service.findById(id));
    }

    @Test
    public void h2UpdateTest() throws ServiceException, DaoException {
        long id = 1;
        String newName = "newName";
        GiftCertificate certificate = new GiftCertificate();
        certificate.setId(id);
        certificate.setName(newName);
        LocalDateTime updateLdt = LocalDateTime.parse("2011-11-11T11:11:11.011");
        LocalDateTime createLdt = LocalDateTime.parse("2001-01-01T01:01:01.001");
        certificate.setLastUpdateDate(updateLdt);

        GiftCertificate expected = new GiftCertificate(id, newName, "description1", 111, 11,
                                                        createLdt, updateLdt, new LinkedHashSet<>());
        expected.getTags().add(new Tag(1, "tag1"));
        expected.getTags().add(new Tag(3, "tag3"));

        assertEquals(expected, service.update(certificate));

        certificate = new GiftCertificate();
        certificate.setId(id);
        certificate.setLastUpdateDate(updateLdt);
        certificate.setTags(new LinkedHashSet<>());
        certificate.getTags().add(new Tag(0, "tag4"));

        expected.setTags(new LinkedHashSet<>());
        expected.getTags().add(new Tag(4, "tag4"));

        assertEquals(expected, service.update(certificate));
    }
}
