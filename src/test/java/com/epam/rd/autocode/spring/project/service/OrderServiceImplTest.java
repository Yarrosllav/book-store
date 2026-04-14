package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.CreateOrderDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.exception.*;
import com.epam.rd.autocode.spring.project.model.*;
import com.epam.rd.autocode.spring.project.model.enums.OrderStatus;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.repo.OrderRepository;
import com.epam.rd.autocode.spring.project.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock private OrderRepository orderRepository;
    @Mock private ClientRepository clientRepository;
    @Mock private EmployeeRepository employeeRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Client client;
    private Employee employee;
    private Book book;
    private Order order;
    private Basket basket;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setName("Clean Code");
        book.setPrice(new BigDecimal("200.00"));

        basket = new Basket();
        basket.setBasketItems(new ArrayList<>());

        client = new Client();
        client.setId(1L);
        client.setEmail("client@example.com");
        client.setBalance(new BigDecimal("1000.00"));
        client.setBasket(basket);
        basket.setClient(client);

        employee = new Employee();
        employee.setId(1L);
        employee.setEmail("employee@example.com");

        order = new Order();
        order.setId(1L);
        order.setClient(client);
        order.setStatus(OrderStatus.PENDING);
        order.setPrice(new BigDecimal("200.00"));
        order.setOrderDate(LocalDateTime.now());
        order.setBookItems(new ArrayList<>());
    }

    @Test
    void getOrdersByClient_ReturnsPageOfOrderDTOs() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(List.of(order));

        when(orderRepository.findAllByClientEmail("client@example.com", pageable)).thenReturn(orderPage);

        Page<OrderDTO> result = orderService.getOrdersByClient("client@example.com", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getClientEmail()).isEqualTo("client@example.com");
    }

    @Test
    void cancelOrderByClient_ValidPendingOrder_RefundsAndSetsStatusRefunded() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderDTO result = orderService.cancelOrderByClient(1L, "client@example.com");

        assertThat(result.getStatus()).isEqualTo(OrderStatus.REFUNDED);
        assertThat(client.getBalance()).isEqualByComparingTo("1200.00");
        verify(orderRepository).save(order);
    }

    @Test
    void cancelOrderByClient_WrongClient_ThrowsAccessDeniedException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrderByClient(1L, "other@example.com"))
                .isInstanceOf(AccessDeniedException.class);

        verify(orderRepository, never()).save(any());
    }

    @Test
    void cancelOrderByClient_OrderNotPending_ThrowsOrderProcessingException() {
        order.setStatus(OrderStatus.CONFIRMED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrderByClient(1L, "client@example.com"))
                .isInstanceOf(OrderProcessingException.class);
    }

    @Test
    void cancelOrderByClient_OrderNotFound_ThrowsOrderNotFoundException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.cancelOrderByClient(99L, "client@example.com"))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void getOrderById_ExistingId_ReturnsOrderDTO() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderDTO result = orderService.getOrderById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getOrderById_NonExistingId_ThrowsOrderNotFoundException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(99L))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void getWaitingOrders_WithSearch_PassesSearchToRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        when(orderRepository.findPendingWithSearch("java", pageable))
                .thenReturn(new PageImpl<>(List.of(order)));

        Page<OrderDTO> result = orderService.getWaitingOrders("java", pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(orderRepository).findPendingWithSearch("java", pageable);
    }

    @Test
    void getWaitingOrders_BlankSearch_PassesNullToRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        when(orderRepository.findPendingWithSearch(null, pageable))
                .thenReturn(new PageImpl<>(List.of()));

        orderService.getWaitingOrders("  ", pageable);

        verify(orderRepository).findPendingWithSearch(null, pageable);
    }

    @Test
    void getWaitingOrders_NullSearch_PassesNullToRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        when(orderRepository.findPendingWithSearch(null, pageable))
                .thenReturn(new PageImpl<>(List.of()));

        orderService.getWaitingOrders(null, pageable);

        verify(orderRepository).findPendingWithSearch(null, pageable);
    }

    @Test
    void getOrderHistoryWithSearch_OnlyMyTrue_PassesEmployeeEmail() {
        Pageable pageable = PageRequest.of(0, 10);
        when(orderRepository.findOrderHistoryWithSearch(null, true, "employee@example.com", pageable))
                .thenReturn(new PageImpl<>(List.of()));

        orderService.getOrderHistoryWithSearch(null, true, "employee@example.com", pageable);

        verify(orderRepository).findOrderHistoryWithSearch(null, true, "employee@example.com", pageable);
    }

    @Test
    void getOrderHistoryWithSearch_NullSearch_PassesNullToRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        when(orderRepository.findOrderHistoryWithSearch(null, false, "emp@example.com", pageable))
                .thenReturn(new PageImpl<>(List.of()));

        orderService.getOrderHistoryWithSearch(null, false, "emp@example.com", pageable);

        verify(orderRepository).findOrderHistoryWithSearch(null, false, "emp@example.com", pageable);
    }

    @Test
    void addOrder_ValidOrder_CreatesOrderAndDeductsBalance() {
        BasketItem basketItem = new BasketItem();
        basketItem.setBook(book);
        basketItem.setQuantity(2);
        basket.getBasketItems().add(basketItem);

        CreateOrderDTO dto = new CreateOrderDTO();
        dto.setClientEmail("client@example.com");

        when(clientRepository.findByEmail("client@example.com")).thenReturn(Optional.of(client));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(10L);
            return o;
        });

        OrderDTO result = orderService.addOrder(dto);

        assertThat(client.getBalance()).isEqualByComparingTo("600.00");
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(basket.getBasketItems()).isEmpty(); // basket cleared
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void addOrder_EmptyBasket_ThrowsOrderProcessingException() {
        CreateOrderDTO dto = new CreateOrderDTO();
        dto.setClientEmail("client@example.com");

        when(clientRepository.findByEmail("client@example.com")).thenReturn(Optional.of(client));

        assertThatThrownBy(() -> orderService.addOrder(dto))
                .isInstanceOf(OrderProcessingException.class);

        verify(orderRepository, never()).save(any());
    }

    @Test
    void addOrder_InsufficientFunds_ThrowsInsufficientFundsException() {
        client.setBalance(new BigDecimal("10.00"));

        BasketItem basketItem = new BasketItem();
        basketItem.setBook(book); // price = 200
        basketItem.setQuantity(1);
        basket.getBasketItems().add(basketItem);

        CreateOrderDTO dto = new CreateOrderDTO();
        dto.setClientEmail("client@example.com");

        when(clientRepository.findByEmail("client@example.com")).thenReturn(Optional.of(client));

        assertThatThrownBy(() -> orderService.addOrder(dto))
                .isInstanceOf(InsufficientFundsException.class);

        verify(orderRepository, never()).save(any());
    }

    @Test
    void addOrder_ClientNotFound_ThrowsClientNotFoundException() {
        CreateOrderDTO dto = new CreateOrderDTO();
        dto.setClientEmail("nobody@example.com");

        when(clientRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.addOrder(dto))
                .isInstanceOf(ClientNotFoundException.class);
    }

    @Test
    void addOrder_NullBasket_ThrowsOrderProcessingException() {
        client.setBasket(null);

        CreateOrderDTO dto = new CreateOrderDTO();
        dto.setClientEmail("client@example.com");

        when(clientRepository.findByEmail("client@example.com")).thenReturn(Optional.of(client));

        assertThatThrownBy(() -> orderService.addOrder(dto))
                .isInstanceOf(OrderProcessingException.class);
    }

    @Test
    void confirmOrder_ValidPendingOrder_SetsConfirmedStatusAndEmployee() {
        order.setEmployee(null);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(employeeRepository.findByEmail("employee@example.com")).thenReturn(Optional.of(employee));

        OrderDTO result = orderService.confirmOrder(1L, "employee@example.com");

        assertThat(result.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(order.getEmployee()).isEqualTo(employee);
    }

    @Test
    void confirmOrder_OrderNotPending_ThrowsOrderProcessingException() {
        order.setStatus(OrderStatus.CONFIRMED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.confirmOrder(1L, "employee@example.com"))
                .isInstanceOf(OrderProcessingException.class);
    }

    @Test
    void confirmOrder_OrderNotFound_ThrowsOrderNotFoundException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.confirmOrder(99L, "employee@example.com"))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void confirmOrder_EmployeeNotFound_ThrowsEmployeeNotFoundException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(employeeRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.confirmOrder(1L, "nobody@example.com"))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void refundOrder_ValidOrder_RefundsAndSetsStatus() {
        order.setEmployee(employee);
        order.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderDTO result = orderService.refundOrder(1L, "employee@example.com");

        assertThat(result.getStatus()).isEqualTo(OrderStatus.REFUNDED);
        assertThat(client.getBalance()).isEqualByComparingTo("1200.00");
    }

    @Test
    void refundOrder_AlreadyRefunded_ThrowsOrderProcessingException() {
        order.setEmployee(employee);
        order.setStatus(OrderStatus.REFUNDED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.refundOrder(1L, "employee@example.com"))
                .isInstanceOf(OrderProcessingException.class);
    }

    @Test
    void refundOrder_WrongEmployee_ThrowsAccessDeniedException() {
        order.setEmployee(employee);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.refundOrder(1L, "other@example.com"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void refundOrder_NoEmployeeAssigned_ThrowsAccessDeniedException() {
        order.setEmployee(null);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.refundOrder(1L, "employee@example.com"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void refundOrder_OrderNotFound_ThrowsOrderNotFoundException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.refundOrder(99L, "employee@example.com"))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void getOrderById_OrderWithBookItems_MapsBookItemsCorrectly() {
        BookItem bookItem = new BookItem();
        bookItem.setId(10L);
        bookItem.setBook(book);
        bookItem.setQuantity(3);
        bookItem.setOrder(order);
        order.getBookItems().add(bookItem);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderDTO result = orderService.getOrderById(1L);

        assertThat(result.getBookItems()).hasSize(1);
        assertThat(result.getBookItems().get(0).getBookId()).isEqualTo(1L);
        assertThat(result.getBookItems().get(0).getBookName()).isEqualTo("Clean Code");
        assertThat(result.getBookItems().get(0).getQuantity()).isEqualTo(3);
    }

    @Test
    void getOrderById_OrderWithEmployee_MapsEmployeeEmailCorrectly() {
        order.setEmployee(employee);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderDTO result = orderService.getOrderById(1L);

        assertThat(result.getEmployeeEmail()).isEqualTo("employee@example.com");
    }

    @Test
    void getOrderById_OrderWithoutEmployee_EmployeeEmailIsNull() {
        order.setEmployee(null);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderDTO result = orderService.getOrderById(1L);

        assertThat(result.getEmployeeEmail()).isNull();
    }
}