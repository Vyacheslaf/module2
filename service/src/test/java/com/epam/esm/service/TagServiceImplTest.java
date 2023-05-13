package com.epam.esm.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.dao.DaoUnsupportedOperationException;
import com.epam.esm.exception.service.ServiceException;
import com.epam.esm.util.RequestParametersHolder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class TagServiceImplTest {

    private static final int TAG_LIST_SIZE = 3;
    private static List<Tag> tagList;
    @Mock
    private TagDao mockDao;
    @InjectMocks
    private TagServiceImpl tagService;

    @BeforeAll
    public static void prepareMockDao() {
        tagList = new ArrayList<>();
        for (int i = 1; i < TAG_LIST_SIZE + 1; i++) {
            Tag tag = new Tag();
            tag.setName("tag" + i);
            tag.setId(i);
            tagList.add(tag);
        }
    }

    @Test
    public void createTest() {
        Tag tag = new Tag();
        tag.setName("tag1");
        when(mockDao.create(tag)).thenReturn(tagList.get(0));

        assertEquals(tagList.get(0), tagService.create(tag));
    }

    @Test
    public void findByIdTest() {
        int id = 2;
        when(mockDao.findById(id)).thenReturn(tagList.get(id - 1));

        assertEquals(tagList.get(id - 1), tagService.findById(id));
    }

    @Test
    public void findAllTest() {
        when(mockDao.findAll(any(RequestParametersHolder.class))).thenReturn(tagList);

        assertEquals(TAG_LIST_SIZE, tagService.findAll(new RequestParametersHolder()).size());
    }

    @Test
    public void deleteTest() {
        int id = 3;
        tagService.delete(id);

        verify(mockDao, atLeastOnce()).delete(anyLong());
    }

    @Test
    public void updateTest() throws ServiceException {
        when(mockDao.update(any(Tag.class))).thenThrow(new DaoUnsupportedOperationException());

        assertThrows(DaoUnsupportedOperationException.class, () -> new TagServiceImpl(mockDao).update(new Tag()));
    }

    @Test
    public void findMostWidelyUsedTagOfUserWithHighestCostOfAllOrdersTest() {
        int id = 3;
        when(mockDao.findMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(anyLong())).thenReturn(tagList.get(id - 1));

        assertEquals(tagList.get(id - 1), tagService.findMostWidelyUsedTagOfUserWithHighestCostOfAllOrders(id));
    }

    @Test
    public void findGiftCertificateTagsTest() {
        when(mockDao.findGiftCertificateTags(anyLong(), any(RequestParametersHolder.class))).thenReturn(tagList);

        long userId = 1;
        assertEquals(TAG_LIST_SIZE, tagService.findGiftCertificateTags(userId, new RequestParametersHolder()).size());
    }
}
