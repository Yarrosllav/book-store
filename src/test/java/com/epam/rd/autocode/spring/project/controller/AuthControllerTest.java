package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.exception.ClientAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest extends BaseControllerTest {

    @Test
    void showLoginPage_ReturnsLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void showRegistrationForm_ReturnsRegisterViewWithEmptyDTO() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("client"));
    }

    @Test
    void registerClient_ValidationErrors_ReturnsRegisterView() throws Exception {
        mockMvc.perform(post("/register").with(csrf())
                        .param("email", "invalid-email")
                        .param("password", "123")
                        .param("name", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasErrors("client"));

        verify(clientService, never()).addClient(any());
    }

    @Test
    void registerClient_DuplicateEmail_ReturnsRegisterViewWithFieldError() throws Exception {
        when(clientService.addClient(any())).thenThrow(new ClientAlreadyExistsException());

        mockMvc.perform(post("/register").with(csrf())
                        .param("email", "existing@example.com")
                        .param("password", "Password123")
                        .param("name", "Alice"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("client", "email"));
    }
}
