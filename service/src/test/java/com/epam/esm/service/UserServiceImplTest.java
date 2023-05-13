package com.epam.esm.service;

import com.epam.esm.dao.sql.UserDaoImpl;
import com.epam.esm.entity.User;
import com.epam.esm.util.RequestParametersHolder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    private static final int USER_LIST_SIZE = 3;
    private static List<User> userList;
    @Mock
    private static UserDaoImpl mockDao;
    @InjectMocks
    private UserServiceImpl userService;

    @BeforeAll
    public static void prepareMockDao() {
        userList = new ArrayList<>();
        for (int i = 1; i < USER_LIST_SIZE + 1; i++) {
            User user = new User();
            user.setUsername("user" + i);
            user.setId(i);
            userList.add(user);
        }
    }

    @Test
    public void findAllTest() {
        when(mockDao.findAll(any(RequestParametersHolder.class))).thenReturn(userList);

        assertEquals(USER_LIST_SIZE, userService.findAll(new RequestParametersHolder()).size());
    }
}
