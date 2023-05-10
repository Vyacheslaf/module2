package com.epam.esm.controller;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.util.GiftCertificateSortMap;
import com.epam.esm.dao.TagDao;
import com.epam.esm.dao.sql.GiftCertificateDaoImpl;
import com.epam.esm.dao.sql.TagDaoImpl;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.exception.dao.DaoException;
import com.epam.esm.exception.dao.DaoWrongIdException;
import com.epam.esm.exception.service.ServiceException;
import com.epam.esm.service.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.util.UriComponentsBuilder;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GiftCertificateControllerTest {

    private static GiftCertificateController controller;
    private GiftCertificateSortMap giftCertificateSortMap;

    @BeforeAll
    public void setupMap() {
        Map<String, String> map = new HashMap<>();
        map.put("name", "gc.name");
        map.put("date", "gc.create_date");
        giftCertificateSortMap = new GiftCertificateSortMap(map);
    }

    @BeforeEach
    public void prepareDataSource() {
        DataSource dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
                                                             .setName("testDB;MODE=MySQL")
                                                             .addScript("test/schema.sql")
                                                             .addScript("test/data.sql")
                                                             .build();
        GiftCertificateDao giftCertificateDao = new GiftCertificateDaoImpl(dataSource, giftCertificateSortMap);
        GiftCertificateService giftCertificateService = new GiftCertificateServiceImpl(giftCertificateDao);
        TagDao tagDao = new TagDaoImpl(dataSource);
        TagService tagService = new TagServiceImpl(tagDao);
        controller = new GiftCertificateController(giftCertificateService, tagService, giftCertificateSortMap);
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
        CollectionModel<GiftCertificate> actualModel;
        actualModel = controller.findAll(null, null, null, 0, 5);

        assertEquals(2, actualModel.getContent().size());
    }

    @Test
    public void findAllEmptyTest() throws ServiceException, DaoException {
        int i = 0;
        controller.delete(++i);
        controller.delete(++i);

        CollectionModel<GiftCertificate> actualModel;
        actualModel = controller.findAll(null, null, null, 0, 5);

        assertEquals(0, actualModel.getContent().size());
    }

    @Test
    public void findByIdTest() throws ServiceException, DaoException {
        long id = 1;

        GiftCertificate actualEntity = controller.findById(id);

        assertEquals("name1", actualEntity.getName());
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
