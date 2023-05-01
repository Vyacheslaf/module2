package com.epam.esm.controller;

import com.epam.esm.dao.Dao;
import com.epam.esm.dao.sql.TagDao;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.dao.DaoDuplicateKeyException;
import com.epam.esm.exception.dao.DaoException;
import com.epam.esm.exception.dao.DaoWrongIdException;
import com.epam.esm.exception.service.ServiceException;
import com.epam.esm.service.Service;
import com.epam.esm.service.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.util.UriComponentsBuilder;

import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TagControllerTest {

    private static final int TAG_LIST_SIZE = 3;
    private static TagController controller;

    @BeforeEach
    public void prepareDataSource() {
        DataSource dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
                                                             .setName("testDB;MODE=MySQL")
                                                             .addScript("test/schema.sql")
                                                             .addScript("test/data.sql")
                                                             .build();
        Dao<Tag> tagDao = new TagDao(dataSource);
        Service<Tag> tagService = new TagService(tagDao);
        controller = new TagController(tagService);
    }

    @Test
    public void deleteTest() throws ServiceException, DaoException {
        long id = 1;
        assertEquals(new ResponseEntity<>(HttpStatus.NO_CONTENT), controller.delete(id));
    }

    @Test
    public void createTest() throws ServiceException, DaoException {
        long id = 4;
        Tag tag = new Tag();
        tag.setName("tag" + id);
        tag.setId(id);
        UriComponentsBuilder ucb = UriComponentsBuilder.fromUriString("");
        BindingResult result = new BeanPropertyBindingResult(null, "");

        ResponseEntity<Tag> actualEntity = controller.create(tag, result, ucb);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucb.path("").build(id));
        ResponseEntity<Tag> expectedEntity = new ResponseEntity<>(tag, headers, HttpStatus.CREATED);

        assertEquals(expectedEntity, actualEntity);
    }

    @Test
    public void findAllNotEmptyTest() throws ServiceException, DaoException {
        List<Tag> tagList = new ArrayList<>();
        for (int i = 1; i < TAG_LIST_SIZE + 1; i++) {
            Tag tag = new Tag();
            tag.setName("tag" + i);
            tag.setId(i);
            tagList.add(tag);
        }

        ResponseEntity<List<Tag>> actualEntity = controller.findAll();

        ResponseEntity<List<Tag>> expectedEntity = new ResponseEntity<>(tagList, HttpStatus.OK);

        assertEquals(expectedEntity, actualEntity);
    }

    @Test
    public void findAllEmptyTest() throws ServiceException, DaoException {
        for (int i = 1; i < TAG_LIST_SIZE + 1; i++) {
            controller.delete(i);
        }

        ResponseEntity<List<Tag>> actualEntity = controller.findAll();

        ResponseEntity<List<Tag>> expectedEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);

        assertEquals(expectedEntity, actualEntity);
    }

    @Test
    public void findByIdTest() throws ServiceException, DaoException {
        long id = 1;
        Tag tag = new Tag();
        tag.setName("tag" + id);
        tag.setId(id);

        ResponseEntity<Tag> actualEntity = controller.findById(id);

        ResponseEntity<Tag> expectedEntity = new ResponseEntity<>(tag, HttpStatus.OK);

        assertEquals(expectedEntity, actualEntity);
    }

    @Test
    public void exceptionsTest() throws ServiceException, DaoException {
        long id = 1;
        Tag tag = new Tag();
        tag.setName("tag" + id);
        UriComponentsBuilder ucb = UriComponentsBuilder.fromUriString("");
        BindingResult result = new BeanPropertyBindingResult(null, "");

        assertThrows(DaoDuplicateKeyException.class, () -> controller.create(tag, result, ucb));

        long wrongId = 4;
        assertThrows(DaoWrongIdException.class, () -> controller.findById(wrongId));
    }
}
