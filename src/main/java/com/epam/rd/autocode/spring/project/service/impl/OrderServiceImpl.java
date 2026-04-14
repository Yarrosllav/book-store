package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookItemDTO;
import com.epam.rd.autocode.spring.project.dto.CreateOrderDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.exception.*;
import com.epam.rd.autocode.spring.project.model.*;
import com.epam.rd.autocode.spring.project.model.enums.OrderStatus;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.repo.OrderRepository;
import com.epam.rd.autocode.spring.project.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrdersByClient(String clientEmail, Pageable pageable) {

        return orderRepository.findAllByClientEmail(clientEmail, pageable)
                .map(this::mapToDTO);
    }

    @Override
    public OrderDTO cancelOrderByClient(Long OrderId, String clientEmail) {

        Order order = orderRepository.findById(OrderId).orElseThrow(OrderNotFoundException::new);

        if(!order.getClient().getEmail().equals(clientEmail)) {
            throw new AccessDeniedException("error.exception.access_denied.order");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new OrderProcessingException("error.exception.order_not_pending_to_cancel");
        }

        order.setStatus(OrderStatus.REFUNDED);

        Client client = order.getClient();
        client.setBalance(client.getBalance().add(order.getPrice()));

        orderRepository.save(order);

        log.info("Order Cancellation: Client [{}] cancelled order ID={}. Refunded {} UAH to balance.", clientEmail, OrderId, order.getPrice());
        return mapToDTO(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrderHistoryWithSearch(String search, boolean onlyMy, String currentEmployeeEmail, Pageable pageable) {
        String finalSearch = (search != null && !search.trim().isEmpty()) ? search : null;

        return orderRepository.findOrderHistoryWithSearch(finalSearch, onlyMy, currentEmployeeEmail, pageable)
                .map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(OrderNotFoundException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> getWaitingOrders(String search, Pageable pageable) {
        String finalSearch = (search != null && !search.trim().isEmpty()) ? search : null;
        return orderRepository.findPendingWithSearch(finalSearch, pageable)
                .map(this::mapToDTO);
    }

    @Override
    @Transactional
    public OrderDTO addOrder(CreateOrderDTO orderDTO) {
        Client client = clientRepository.findByEmail(orderDTO.getClientEmail())
                .orElseThrow(ClientNotFoundException::new);

        Basket basket = client.getBasket();
        if(basket == null || basket.getBasketItems().isEmpty()) {
            throw new OrderProcessingException("error.exception.basket_empty");
        }

        BigDecimal totalPrice = basket.getBasketItems().stream()
                .map(item -> item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if(client.getBalance().compareTo(totalPrice) < 0) {
            log.warn("Failed order attempt: Client [{}] has insufficient funds (Required: {}, Balance: {})",
                    client.getEmail(), totalPrice, client.getBalance());
            throw new InsufficientFundsException("error.exception.insufficient_funds");
        }

        client.setBalance(client.getBalance().subtract(totalPrice));

        Order order = new Order();
        order.setClient(client);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setEmployee(null);

        List<BookItem> bookItems = new ArrayList<>();

        for (BasketItem basketItem : basket.getBasketItems()) {
            BookItem bookItem = new BookItem();
            bookItem.setBook(basketItem.getBook());
            bookItem.setQuantity(basketItem.getQuantity());
            bookItem.setOrder(order);
            bookItems.add(bookItem);
        }

        order.setBookItems(bookItems);
        order.setPrice(totalPrice);

        basket.getBasketItems().clear();

        Order savedOrder = orderRepository.save(order);

        log.info("New Order Created: ID={} by client [{}]. Total: {} UAH", savedOrder.getId(), client.getEmail(), totalPrice);
        return mapToDTO(savedOrder);
    }

    @Override
    @Transactional
    public OrderDTO confirmOrder(Long id, String employeeEmail) {
        Order order = orderRepository.findById(id).orElseThrow(OrderNotFoundException::new);

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new OrderProcessingException("error.exception.order_not_pending");
        }

        Employee employee = employeeRepository.findByEmail(employeeEmail).orElseThrow(EmployeeNotFoundException::new);

        order.setEmployee(employee);
        order.setStatus(OrderStatus.CONFIRMED);

        log.info("Order Confirmation: Employee [{}] confirmed order ID={}", employeeEmail, id);
        return mapToDTO(order);
    }

    @Override
    @Transactional
    public OrderDTO refundOrder(Long id, String employeeEmail) {
        Order order = orderRepository.findById(id).orElseThrow(OrderNotFoundException::new);

        if (order.getEmployee() == null || !order.getEmployee().getEmail().equals(employeeEmail)) {
            throw new AccessDeniedException("error.exception.access_denied.order");
        }

        if (order.getStatus() == OrderStatus.REFUNDED) {
            throw new OrderProcessingException("error.exception.order_already_refunded");
        }

        order.setStatus(OrderStatus.REFUNDED);

        Client client = order.getClient();
        client.setBalance(client.getBalance().add(order.getPrice()));

        log.warn("Admin Refund: Employee [{}] refunded order ID={}. {} UAH returned to client [{}]",
                employeeEmail, id, order.getPrice(), client.getEmail());
        return mapToDTO(order);
    }

    private OrderDTO mapToDTO(Order order) {
        OrderDTO dto = new OrderDTO();

        dto.setId(order.getId());
        dto.setClientEmail(order.getClient().getEmail());
        if (order.getEmployee() != null) {
            dto.setEmployeeEmail(order.getEmployee().getEmail());
        }
        dto.setOrderDate(order.getOrderDate());
        dto.setPrice(order.getPrice());
        dto.setStatus(order.getStatus());

        if (order.getBookItems() != null) {
            List<BookItemDTO> itemDTOs = order.getBookItems().stream()
                    .map(item -> {
                        BookItemDTO itemDto = new BookItemDTO();
                        itemDto.setId(item.getId());
                        itemDto.setBookId(item.getBook().getId());
                        itemDto.setBookName(item.getBook().getName());
                        itemDto.setQuantity(item.getQuantity());
                        return itemDto;
                    })
                    .collect(Collectors.toList());
            dto.setBookItems(itemDTOs);
        }
        return dto;
    }
}
