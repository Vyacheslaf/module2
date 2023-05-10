package com.epam.esm.controller;

import com.epam.esm.dao.TagDao;
import com.epam.esm.dao.sql.TagDaoImpl;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.dao.DaoDuplicateKeyException;
import com.epam.esm.exception.dao.DaoException;
import com.epam.esm.exception.dao.DaoWrongIdException;
import com.epam.esm.exception.service.ServiceException;
import com.epam.esm.service.TagService;
import com.epam.esm.service.TagServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.RepresentationModel;
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
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
        TagDao tagDao = new TagDaoImpl(dataSource);
        TagService tagService = new TagServiceImpl(tagDao);
        controller = new TagController(tagService);
    }

    @Test
    public void deleteTest() throws ServiceException, DaoException {
        long id = 1;
        assertEquals(ResponseEntity.ok(new RepresentationModel<>()
                                    .add(linkTo(methodOn(TagController.class).findAll(0, 5)).withRel("rrr"))),
                     controller.delete(id));
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
            Link link = linkTo(methodOn(TagController.class).findById(i)).withSelfRel();
            tag.add(link);
            tagList.add(tag);
        }

        int page = 0;
        int size = 1000;
        ResponseEntity<CollectionModel<Tag>> actualEntity = controller.findAll(page, size);

        Link link = linkTo(methodOn(TagController.class).findAll(page, size)).withSelfRel();
        CollectionModel<Tag> collectionModel = CollectionModel.of(tagList, link);
        ResponseEntity<CollectionModel<Tag>> expectedEntity = new ResponseEntity<>(collectionModel, HttpStatus.OK);

        assertEquals(expectedEntity, actualEntity);
    }

    @Test
    public void findAllEmptyTest() throws ServiceException, DaoException {
        for (int i = 1; i < TAG_LIST_SIZE + 1; i++) {
            controller.delete(i);
        }

        int page = 0;
        int size = 1000;
        ResponseEntity<CollectionModel<Tag>> actualEntity = controller.findAll(page, size);

        Link link = linkTo(methodOn(TagController.class).findAll(page, size)).withSelfRel();
        ResponseEntity<CollectionModel<Tag>> expectedEntity
                = new ResponseEntity<>(CollectionModel.of(new ArrayList<Tag>(), link), HttpStatus.OK);

        assertEquals(expectedEntity, actualEntity);
    }

    @Test
    public void findByIdTest() throws ServiceException, DaoException {
        long id = 1;
        Tag tag = new Tag();
        tag.setName("tag" + id);
        tag.setId(id);

        tag.add(linkTo(TagController.class).slash(id).withSelfRel());
        tag.add(linkTo(TagController.class).withRel(LinkRelation.of("parent")));
        Tag actualEntity = controller.findById(id);

        Tag expectedEntity = tag;

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
