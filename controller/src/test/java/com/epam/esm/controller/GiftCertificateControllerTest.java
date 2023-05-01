package com.epam.esm.controller;

import com.epam.esm.dao.Dao;
import com.epam.esm.dao.sql.GiftCertificateDao;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.exception.dao.DaoException;
import com.epam.esm.exception.dao.DaoWrongIdException;
import com.epam.esm.exception.service.ServiceException;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.util.UriComponentsBuilder;

import javax.sql.DataSource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GiftCertificateControllerTest {

    private static GiftCertificateController controller;

    @BeforeEach
    public void prepareDataSource() {
        DataSource dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
                                                             .setName("testDB;MODE=MySQL")
                                                             .addScript("test/schema.sql")
                                                             .addScript("test/data.sql")
                                                             .build();
        Dao<GiftCertificate> dao = new GiftCertificateDao(dataSource);
        Service<GiftCertificate> service = new GiftCertificateService(dao);
        controller = new GiftCertificateController(service);
    }

    @Test
    public void deleteTest() throws ServiceException, DaoException {
        long id = 1;
        assertEquals(new ResponseEntity<>(HttpStatus.NO_CONTENT), controller.delete(id));
    }

    @Test
    public void createTest() throws ServiceException, DaoException {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setName("some_name");
        certificate.setDescription("some_description");
        certificate.setPrice(0);
        certificate.setDuration(0);
        UriComponentsBuilder ucb = UriComponentsBuilder.fromUriString("");
        BindingResult result = new BeanPropertyBindingResult(null, "");

        ResponseEntity<GiftCertificate> actualEntity = controller.create(certificate, result, ucb);

        assertEquals(HttpStatus.CREATED, actualEntity.getStatusCode());
    }

    @Test
    public void findAllNotEmptyTest() throws ServiceException, DaoException {
        ResponseEntity<List<GiftCertificate>> actualEntity;
        actualEntity = controller.findAll(null, null, null, null);

        assertEquals(HttpStatus.OK, actualEntity.getStatusCode());
    }

    @Test
    public void findAllEmptyTest() throws ServiceException, DaoException {
        int i = 0;
        controller.delete(++i);
        controller.delete(++i);

        ResponseEntity<List<GiftCertificate>> actualEntity;
        actualEntity = controller.findAll(null, null, null, null);

        assertEquals(HttpStatus.NO_CONTENT, actualEntity.getStatusCode());
    }

    @Test
    public void findByIdTest() throws ServiceException, DaoException {
        long id = 1;

        ResponseEntity<GiftCertificate> actualEntity = controller.findById(id);

        assertEquals(HttpStatus.OK, actualEntity.getStatusCode());
    }

    @Test
    public void updateTest() throws ServiceException, DaoException {
        long id = 1;
        GiftCertificate certificate = new GiftCertificate();
        certificate.setName("new_name");
        certificate.setId(id);
        UriComponentsBuilder ucb = UriComponentsBuilder.fromUriString("");
        BindingResult result = new BeanPropertyBindingResult(null, "");

        ResponseEntity<GiftCertificate> actualEntity = controller.update(id, certificate, ucb);

        assertEquals(HttpStatus.OK, actualEntity.getStatusCode());
    }

    @Test
    public void exceptionsTest() throws ServiceException, DaoException {
        long wrongId = 3;
        GiftCertificate certificate = new GiftCertificate();
        certificate.setName("new_name");
        certificate.setId(wrongId);

        UriComponentsBuilder ucb = UriComponentsBuilder.fromUriString("");

        assertThrows(DaoWrongIdException.class, () -> controller.findById(wrongId));
        assertThrows(DaoWrongIdException.class, () -> controller.update(wrongId, certificate, ucb));
    }
}
