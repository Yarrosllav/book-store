package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.model.enums.OrderStatus;
import com.epam.rd.autocode.spring.project.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest extends BaseControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean
    OrderService orderService;

    private OrderDTO buildOrder(String clientEmail) {
        OrderDTO dto = new OrderDTO();
        dto.setId(1L);
        dto.setClientEmail(clientEmail);
        dto.setStatus(OrderStatus.PENDING);
        dto.setPrice(new BigDecimal("200.00"));
        dto.setOrderDate(LocalDateTime.now());
        dto.setBookItems(List.of());
        return dto;
    }

    @Test
    @WithMockUser(username = "client@example.com", roles = "CLIENT")
    void getOrderDetails_OwnOrder_ReturnsDetailsView() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(buildOrder("client@example.com"));

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/details"))
                .andExpect(model().attributeExists("order"));
    }

    @Test
    @WithMockUser(username = "other@example.com", roles = "CLIENT")
    void getOrderDetails_OtherClientOrder_IsForbidden() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(buildOrder("client@example.com"));

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "emp@example.com", roles = "EMPLOYEE")
    void getOrderDetails_AsEmployee_CanViewAnyOrder() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(buildOrder("client@example.com"));

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/details"));
    }

    @Test
    void getOrderDetails_Unauthenticated_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/orders/1")).andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "client@example.com", roles = "CLIENT")
    void checkout_AsClient_PlacesOrderAndRedirects() throws Exception {
        when(orderService.addOrder(any())).thenReturn(buildOrder("client@example.com"));

        mockMvc.perform(post("/orders/checkout").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/my?success=orderPlaced"));

        verify(orderService).addOrder(argThat(dto -> "client@example.com".equals(dto.getClientEmail())));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void checkout_AsEmployee_IsForbidden() throws Exception {
        mockMvc.perform(post("/orders/checkout").with(csrf())).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "client@example.com", roles = "CLIENT")
    void myOrders_AsClient_ReturnsClientOrdersView() throws Exception {
        when(orderService.getOrdersByClient(eq("client@example.com"), any())).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/orders/my"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/client-orders"))
                .andExpect(model().attributeExists("orders", "currentPage", "totalPages", "sortBy", "sortDir"));
    }

    @Test
    @WithMockUser(username = "client@example.com", roles = "CLIENT")
    void myOrders_DescSort_SetsModelAttributes() throws Exception {
        when(orderService.getOrdersByClient(any(), any())).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/orders/my").param("sortDir", "desc"))
                .andExpect(model().attribute("sortDir", "desc"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void myOrders_AsEmployee_IsForbidden() throws Exception {
        mockMvc.perform(get("/orders/my")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "client@example.com", roles = "CLIENT")
    void cancelOrder_AsClient_CancelsAndRedirects() throws Exception {
        when(orderService.cancelOrderByClient(1L, "client@example.com"))
                .thenReturn(buildOrder("client@example.com"));

        mockMvc.perform(post("/orders/1/cancel").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/my?success=cancelled"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void cancelOrder_AsEmployee_IsForbidden() throws Exception {
        mockMvc.perform(post("/orders/1/cancel").with(csrf())).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void waitingOrders_AsEmployee_ReturnsWaitingOrdersView() throws Exception {
        when(orderService.getWaitingOrders(any(), any())).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/orders/waiting"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/waiting-orders"))
                .andExpect(model().attributeExists("orders", "currentPage", "totalPages", "sortDir"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void waitingOrders_WithSearch_PassesSearchToModel() throws Exception {
        when(orderService.getWaitingOrders(eq("java"), any())).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/orders/waiting").param("search", "java"))
                .andExpect(model().attribute("search", "java"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void waitingOrders_AscSortDir_SetsModelAttribute() throws Exception {
        when(orderService.getWaitingOrders(any(), any())).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/orders/waiting").param("sortDir", "asc"))
                .andExpect(model().attribute("sortDir", "asc"));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void waitingOrders_AsClient_IsForbidden() throws Exception {
        mockMvc.perform(get("/orders/waiting")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "emp@example.com", roles = "EMPLOYEE")
    void processedOrders_AsEmployee_ReturnsEmployeeOrdersView() throws Exception {
        when(orderService.getOrderHistoryWithSearch(any(), anyBoolean(), any(), any()))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/orders/processed"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/employee-orders"))
                .andExpect(model().attributeExists("orders", "currentPage", "totalPages", "onlyMy", "sortDir"));
    }

    @Test
    @WithMockUser(username = "emp@example.com", roles = "EMPLOYEE")
    void processedOrders_OnlyMyTrue_PassesTrueToService() throws Exception {
        when(orderService.getOrderHistoryWithSearch(any(), eq(true), eq("emp@example.com"), any()))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/orders/processed").param("onlyMy", "true"))
                .andExpect(model().attribute("onlyMy", true));

        verify(orderService).getOrderHistoryWithSearch(any(), eq(true), eq("emp@example.com"), any());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void processedOrders_AsClient_IsForbidden() throws Exception {
        mockMvc.perform(get("/orders/processed")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "emp@example.com", roles = "EMPLOYEE")
    void confirmOrder_AsEmployee_ConfirmsAndRedirects() throws Exception {
        when(orderService.confirmOrder(1L, "emp@example.com")).thenReturn(buildOrder("client@example.com"));

        mockMvc.perform(post("/orders/1/confirm").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/waiting?success=confirmed"));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void confirmOrder_AsClient_IsForbidden() throws Exception {
        mockMvc.perform(post("/orders/1/confirm").with(csrf())).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "emp@example.com", roles = "EMPLOYEE")
    void refundOrder_AsEmployee_RefundsAndRedirects() throws Exception {
        when(orderService.refundOrder(1L, "emp@example.com")).thenReturn(buildOrder("client@example.com"));

        mockMvc.perform(post("/orders/1/refund").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/processed?success=refunded"));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void refundOrder_AsClient_IsForbidden() throws Exception {
        mockMvc.perform(post("/orders/1/refund").with(csrf())).andExpect(status().isNotFound());
    }
}
