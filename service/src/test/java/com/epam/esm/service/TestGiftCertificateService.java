package com.epam.esm.service;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.sql.GiftCertificateDaoImpl;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.util.RequestParametersHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestGiftCertificateService {
    private static final int H2_CERTIFICATE_LIST_SIZE = 2;
    private RequestParametersHolder rph = new RequestParametersHolder(0, 5);
    private DataSource dataSource;
    private GiftCertificateServiceImpl service;

    @BeforeEach
    public void prepareDataSource() {
        dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
                .setName("testDB;MODE=MySQL")
                .addScript("test/schema.sql")
                .addScript("test/data.sql")
                .build();
        GiftCertificateDao dao = new GiftCertificateDaoImpl(dataSource);
        service = new GiftCertificateServiceImpl(dao);
    }

    @Test
    public void h2FindAllTest() {
        assertEquals(H2_CERTIFICATE_LIST_SIZE, service.findAll(rph).size());
    }

    @Test
    public void h2DeleteTest() {
        long id = 1;
        service.delete(id);
        assertEquals(H2_CERTIFICATE_LIST_SIZE - 1, service.findAll(rph).size());
    }

    @Test
    public void h2CreateTest() {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setName("some_name");
        certificate.setDescription("some_description");
        certificate.setPrice(0);
        certificate.setDuration(0);

        long expectedValue = H2_CERTIFICATE_LIST_SIZE + 1;
        assertEquals(expectedValue, service.create(certificate).getId());
        assertEquals(expectedValue, service.findAll(rph).size());

        certificate.getTags().add(new Tag(0, "tag1"));
        certificate.getTags().add(new Tag(0, "tag22"));

        expectedValue++;
        assertEquals(expectedValue, service.create(certificate).getId());
        assertEquals(expectedValue, service.findAll(rph).size());
        int tagsCount = new JdbcTemplate(dataSource).queryForObject("SELECT COUNT(id) FROM tag", Integer.class);
        assertEquals(expectedValue, tagsCount);
    }

    @Test
    public void h2FindByIdTest() {
        long id = 1;
        LocalDateTime ldt = LocalDateTime.parse("2001-01-01T01:01:01.001");
        GiftCertificate expected = new GiftCertificate(id, "name1", "description1",
                111, 11, ldt, ldt, null);

        assertEquals(expected, service.findById(id));
    }

    @Test
    public void h2UpdateTest() {
        long id = 1;
        String newName = "newName";
        GiftCertificate certificate = new GiftCertificate();
        certificate.setId(id);
        certificate.setName(newName);
        LocalDateTime updateLdt = LocalDateTime.parse("2011-11-11T11:11:11.011");
        LocalDateTime createLdt = LocalDateTime.parse("2001-01-01T01:01:01.001");
        certificate.setLastUpdateDate(updateLdt);

        GiftCertificate expected = new GiftCertificate(id, newName, "description1", 111, 11,
                createLdt, updateLdt, null);

        assertEquals(expected, service.update(certificate));
    }
}
