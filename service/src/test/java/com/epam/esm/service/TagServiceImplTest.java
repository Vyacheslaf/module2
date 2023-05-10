package com.epam.esm.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.epam.esm.dao.Dao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.dao.DaoException;
import com.epam.esm.exception.service.ServiceException;
import com.epam.esm.exception.service.ServiceUnsupportedOperationException;
import com.epam.esm.util.RequestParametersHolder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class TagServiceImplTest {

    private static final int TAG_LIST_SIZE = 3;
    private static TagDao mockDao;
    private static List<Tag> tagList;

    @BeforeAll
    public static void prepareMockDao() {
        mockDao = Mockito.mock(TagDao.class);
        tagList = new ArrayList<>();
        for (int i = 1; i < TAG_LIST_SIZE + 1; i++) {
            Tag tag = new Tag();
            tag.setName("tag" + i);
            tag.setId(i);
            tagList.add(tag);
        }
    }

    @Test
    public void createTest() throws DaoException, ServiceException {
        Tag tag = new Tag();
        tag.setName("tag1");
        when(mockDao.create(tag)).thenReturn(tagList.get(0));

        TagServiceImpl tagService = new TagServiceImpl(mockDao);

        assertEquals(tagList.get(0), tagService.create(tag));
    }

    @Test
    public void findByIdTest() throws DaoException, ServiceException {
        int id = 2;
        when(mockDao.findById(id)).thenReturn(tagList.get(id - 1));

        TagServiceImpl tagService = new TagServiceImpl(mockDao);
        assertEquals(tagList.get(id - 1), tagService.findById(id));
    }

    @Test
    public void findAllTest() throws DaoException, ServiceException {
        when(mockDao.findAll(any(RequestParametersHolder.class))).thenReturn(tagList);

        TagServiceImpl tagService = new TagServiceImpl(mockDao);

        assertEquals(TAG_LIST_SIZE, tagService.findAll(new RequestParametersHolder()).size());
    }

    @Test
    public void deleteTest() throws DaoException, ServiceException {
        int id = 3;
        TagServiceImpl tagService = new TagServiceImpl(mockDao);
        tagService.delete(id);
        verify(mockDao, atLeastOnce()).delete(anyLong());
    }

    @Test
    public void updateTest() throws ServiceException {
        assertThrows(ServiceUnsupportedOperationException.class, () -> new TagServiceImpl(mockDao).update(new Tag()));
    }
}
