package com.epam.esm.controller;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.dao.sql.GiftCertificateDaoImpl;
import com.epam.esm.dao.sql.TagDaoImpl;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.exception.controller.NoContentException;
import com.epam.esm.exception.dao.DaoException;
import com.epam.esm.exception.dao.DaoWrongIdException;
import com.epam.esm.exception.service.ServiceException;
import com.epam.esm.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.CollectionModel;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GiftCertificateControllerTest {

    private static GiftCertificateController controller;
    private static TagService tagService;
    private static GiftCertificateService giftCertificateService;

    @BeforeEach
    public void prepareDataSource() {
        DataSource dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
                                                             .setName("testDB;MODE=MySQL")
                                                             .addScript("test/schema.sql")
                                                             .addScript("test/data.sql")
                                                             .build();
        GiftCertificateDao giftCertificateDao = new GiftCertificateDaoImpl(dataSource);
        giftCertificateService = new GiftCertificateServiceImpl(giftCertificateDao);
        TagDao tagDao = new TagDaoImpl(dataSource);
        tagService = new TagServiceImpl(tagDao);
        controller = new GiftCertificateController(giftCertificateService, tagService);
    }

    @Test
    public void deleteTest() throws ServiceException, DaoException {
        long id = 1;
        controller.delete(id);
        assertThrows(DaoWrongIdException.class,
                    () -> new GiftCertificateController(giftCertificateService, tagService).findById(id));
    }

    @Test
    public void createTest() throws ServiceException, DaoException {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setName("some_name");
        certificate.setDescription("some_description");
        certificate.setPrice(0);
        certificate.setDuration(0);
        BindingResult result = new BeanPropertyBindingResult(null, "");

        GiftCertificate actual = controller.create(certificate, result);
        assertEquals(certificate.getName(), actual.getName());
        assertEquals(3, actual.getId());
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

        int page = 0;
        int size = 1000;

        assertThrows(NoContentException.class,
                        () -> new GiftCertificateController(giftCertificateService, tagService)
                                    .findAll(null, null, null, page, size));
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
        BindingResult result = new BeanPropertyBindingResult(null, "");

        assertEquals(certificate.getName(), controller.update(id, certificate).getName());
    }

    @Test
    public void exceptionsTest() throws ServiceException, DaoException {
        long wrongId = 3;
        GiftCertificate certificate = new GiftCertificate();
        certificate.setName("new_name");
        certificate.setId(wrongId);

        assertThrows(DaoWrongIdException.class, () -> controller.findById(wrongId));
        assertThrows(DaoWrongIdException.class, () -> controller.update(wrongId, certificate));
    }
}
