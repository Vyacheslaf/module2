package com.epam.esm.service;

import com.epam.esm.dao.OrderDao;
import com.epam.esm.entity.Order;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {
    private static final int ORDER_LIST_SIZE = 3;
    private static List<Order> orderList;
    @Mock
    private OrderDao mockDao;
    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeAll
    public static void prepareMockDao() {
        orderList = new ArrayList<>();
        for (int i = 1; i < ORDER_LIST_SIZE + 1; i++) {
            Order order = new Order();
            order.setUserId(i);
            order.setId(i);
            orderList.add(order);
        }
    }

    @Test
    public void findAllByUserIdTest() {
        when(mockDao.findAllByUserId(anyLong(), anyInt(), anyInt())).thenReturn(orderList);

        assertEquals(ORDER_LIST_SIZE, orderService.findAllByUserId(0, 0, 0).size());
    }

    @Test
    public void findByUserAndOrderIdsTest() {
        int id = 2;
        when(mockDao.findByUserAndOrderIds(id, id)).thenReturn(orderList.get(id - 1));

        assertEquals(orderList.get(id - 1), orderService.findByUserAndOrderIds(id, id));
    }

    @Test
    public void createTest() {
        Order order = new Order();
        when(mockDao.create(order)).thenReturn(order);

        assertEquals(LocalDateTime.now(ZoneId.of("UTC")).getYear(),
                     orderService.create(order).getPurchaseDate().getYear());
    }
}
