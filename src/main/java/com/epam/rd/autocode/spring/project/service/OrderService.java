package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.CreateOrderDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;

public interface OrderService {

    Page<OrderDTO> getOrdersByClient(String clientEmail, Pageable pageable);

    OrderDTO cancelOrderByClient(Long OrderId, String clientEmail);

    Page<OrderDTO> getOrderHistoryWithSearch(String search, boolean onlyMy, String currentEmployeeEmail, Pageable pageable);

    OrderDTO getOrderById(Long id);

    OrderDTO addOrder(CreateOrderDTO order);

    OrderDTO confirmOrder(Long id, String employeeEmail);

    Page<OrderDTO> getWaitingOrders(String search, Pageable pageable);

    OrderDTO refundOrder(Long id, String employeeEmail);
}
