package com.epam.rd.autocode.spring.project.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
class AdminControllerTest extends BaseControllerTest {

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getAllClients_AsEmployee_ReturnsClientsListView() throws Exception {
        when(clientService.searchClients(any(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/admin/clients"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/clients-list"))
                .andExpect(model().attributeExists("clients", "currentPage", "totalPages"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getAllClients_WithSearch_PassesSearchToModel() throws Exception {
        when(clientService.searchClients(eq("john"), any())).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/admin/clients").param("search", "john"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("search", "john"));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void getAllClients_AsClient_IsForbidden() throws Exception {
        mockMvc.perform(get("/admin/clients")).andExpect(status().isNotFound());
    }

    @Test
    void getAllClients_Unauthenticated_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/admin/clients")).andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void blockClient_RedirectsWithSuccess() throws Exception {
        mockMvc.perform(post("/admin/clients/1/block").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/clients?success=blocked"));
        verify(clientService).blockClient(1L);
    }
}