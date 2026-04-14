package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BasketDTO;
import com.epam.rd.autocode.spring.project.dto.BasketItemDTO;
import com.epam.rd.autocode.spring.project.service.BasketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BasketController.class)
class BasketControllerTest extends BaseControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean
    BasketService basketService;

    @Test
    @WithMockUser(username = "client@example.com", roles = "CLIENT")
    void viewBasket_AsClient_ReturnsBasketView() throws Exception {
        when(basketService.getBasketByClientEmail("client@example.com"))
                .thenReturn(new BasketDTO(List.of(), BigDecimal.ZERO));

        mockMvc.perform(get("/basket"))
                .andExpect(status().isOk())
                .andExpect(view().name("basket/view"))
                .andExpect(model().attributeExists("basket"));
    }

    @Test
    @WithMockUser(username = "client@example.com", roles = "CLIENT")
    void viewBasket_WithItems_PopulatesModel() throws Exception {
        BasketItemDTO item = BasketItemDTO.builder()
                .id(1L).bookId(10L).bookName("Clean Code")
                .price(new BigDecimal("200.00")).quantity(2)
                .totalItemPrice(new BigDecimal("400.00")).build();
        BasketDTO basketDTO = new BasketDTO(List.of(item), new BigDecimal("400.00"));
        when(basketService.getBasketByClientEmail("client@example.com")).thenReturn(basketDTO);

        mockMvc.perform(get("/basket"))
                .andExpect(model().attribute("basket", basketDTO));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void viewBasket_AsEmployee_IsForbidden() throws Exception {
        mockMvc.perform(get("/basket")).andExpect(status().isNotFound());
    }

    @Test
    void viewBasket_Unauthenticated_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/basket")).andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "client@example.com", roles = "CLIENT")
    void addBook_DefaultQuantity_CallsServiceAndRedirects() throws Exception {
        mockMvc.perform(post("/basket/add/5").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books?success=addedToBasket"));

        verify(basketService).addBookToBasket("client@example.com", 5L, 1);
    }

    @Test
    @WithMockUser(username = "client@example.com", roles = "CLIENT")
    void addBook_CustomQuantity_PassesQuantityToService() throws Exception {
        mockMvc.perform(post("/basket/add/5").with(csrf()).param("quantity", "3"))
                .andExpect(status().is3xxRedirection());

        verify(basketService).addBookToBasket("client@example.com", 5L, 3);
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void addBook_AsEmployee_IsForbidden() throws Exception {
        mockMvc.perform(post("/basket/add/5").with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "client@example.com", roles = "CLIENT")
    void removeBook_AsClient_RedirectsToBasket() throws Exception {
        mockMvc.perform(post("/basket/remove/10").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/basket?success=removed"));

        verify(basketService).removeBookFromBasket("client@example.com", 10L);
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void removeBook_AsEmployee_IsForbidden() throws Exception {
        mockMvc.perform(post("/basket/remove/10").with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "client@example.com", roles = "CLIENT")
    void updateQuantity_PositiveDelta_CallsService() throws Exception {
        mockMvc.perform(post("/basket/update/10").with(csrf()).param("delta", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/basket"));

        verify(basketService).updateQuantity("client@example.com", 10L, 1);
    }

    @Test
    @WithMockUser(username = "client@example.com", roles = "CLIENT")
    void updateQuantity_NegativeDelta_CallsServiceWithNegative() throws Exception {
        mockMvc.perform(post("/basket/update/10").with(csrf()).param("delta", "-1"))
                .andExpect(status().is3xxRedirection());

        verify(basketService).updateQuantity("client@example.com", 10L, -1);
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void updateQuantity_AsEmployee_IsForbidden() throws Exception {
        mockMvc.perform(post("/basket/update/10").with(csrf()).param("delta", "1"))
                .andExpect(status().isNotFound());
    }
}
