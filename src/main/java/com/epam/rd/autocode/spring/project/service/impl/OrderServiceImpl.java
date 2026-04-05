package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.*;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.repo.OrderRepository;
import com.epam.rd.autocode.spring.project.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final ModelMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByClient(String clientEmail) {
        log.info("Getting orders by client: {}", clientEmail);

        return orderRepository.findAllByClientEmail(clientEmail).stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByEmployee(String employeeEmail) {
        log.info("Getting orders by employee: {}", employeeEmail);

        return orderRepository.findAllByEmployeeEmail(employeeEmail).stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional
    public OrderDTO addOrder(OrderDTO orderDTO) {
        log.info("Adding order for client: {}", orderDTO.getClientEmail());

        Client client = clientRepository.findByEmail(orderDTO.getClientEmail())
                .orElseThrow(NotFoundException::new);

        Basket basket = client.getBasket();
        if(basket == null || basket.getBasketItems().isEmpty()) {
            throw new IllegalStateException("Client basket is empty");
        }

        Order order = new Order();
        order.setClient(client);
        order.setOrderDate(LocalDateTime.now());
        order.setEmployee(null);

        BigDecimal totalPrice = BigDecimal.ZERO;
        List<BookItem> bookItems = new ArrayList<>();

        for (BasketItem basketItem : basket.getBasketItems()) {
            BookItem bookItem = new BookItem();
            bookItem.setBook(basketItem.getBook());
            bookItem.setQuantity(basketItem.getQuantity());
            bookItem.setOrder(order);
            bookItems.add(bookItem);

            BigDecimal itemCost = basketItem.getBook().getPrice()
                    .multiply(BigDecimal.valueOf(basketItem.getQuantity()));
            totalPrice = totalPrice.add(itemCost);
        }

        order.setBookItems(bookItems);
        order.setPrice(totalPrice);

        basket.getBasketItems().clear();

        Order savedOrder = orderRepository.save(order);

        return mapToDTO(savedOrder);
    }

    @Override
    @Transactional
    public OrderDTO confirmOrder(Long id, String employeeEmail) {
        log.info("Employee {} confirming order {}",employeeEmail, id);

        Order order = orderRepository.findById(id).orElseThrow(NotFoundException::new);
        Employee employee = employeeRepository.findByEmail(employeeEmail).orElseThrow(NotFoundException::new);

        order.setEmployee(employee);

        return mapper.map(order, OrderDTO.class);
    }

    private OrderDTO mapToDTO(Order order) {
        OrderDTO dto = mapper.map(order, OrderDTO.class);

        dto.setClientEmail(order.getClient().getEmail());

        if(order.getEmployee() != null) {
            dto.setEmployeeEmail(order.getEmployee().getEmail());
        }

        return dto;
    }
}
