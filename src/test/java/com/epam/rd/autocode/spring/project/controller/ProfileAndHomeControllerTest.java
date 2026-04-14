package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.dto.UpdateClientDTO;
import com.epam.rd.autocode.spring.project.dto.UpdateEmployeeDTO;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)
class ProfileControllerTest extends BaseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    EmployeeService employeeService;

    private ClientDTO buildClientDTO() {
        ClientDTO dto = new ClientDTO();
        dto.setId(1L);
        dto.setEmail("client@example.com");
        dto.setName("Alice");
        dto.setBalance(new BigDecimal("100.00"));
        return dto;
    }

    private EmployeeDTO buildEmployeeDTO() {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(2L);
        dto.setEmail("emp@example.com");
        dto.setName("Bob");
        dto.setBirthDate(LocalDate.of(1990, 1, 1));
        dto.setPhone("+380991234567");
        return dto;
    }

    @Test
    @WithMockUser(username = "client@example.com", roles = "CLIENT")
    void viewProfile_AsClient_ReturnsProfileViewWithClientRole() throws Exception {
        when(clientService.getClientByEmail("client@example.com")).thenReturn(buildClientDTO());

        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/view"))
                .andExpect(model().attribute("role", "CLIENT"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser(username = "emp@example.com", roles = "EMPLOYEE")
    void viewProfile_AsEmployee_ReturnsProfileViewWithEmployeeRole() throws Exception {
        when(employeeService.getEmployeeByEmail("emp@example.com")).thenReturn(buildEmployeeDTO());

        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/view"))
                .andExpect(model().attribute("role", "EMPLOYEE"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void viewProfile_Unauthenticated_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/profile")).andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "client@example.com", roles = "CLIENT")
    void showEditForm_AsClient_ReturnsEditClientView() throws Exception {
        when(clientService.getClientByEmail("client@example.com")).thenReturn(buildClientDTO());

        mockMvc.perform(get("/profile/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/edit-client"))
                .andExpect(model().attributeExists("clientDto"));
    }

    @Test
    @WithMockUser(username = "emp@example.com", roles = "EMPLOYEE")
    void showEditForm_AsEmployee_ReturnsEditEmployeeView() throws Exception {
        when(employeeService.getEmployeeByEmail("emp@example.com")).thenReturn(buildEmployeeDTO());

        mockMvc.perform(get("/profile/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/edit-employee"))
                .andExpect(model().attributeExists("employeeDto"));
    }

    @Test
    @WithMockUser(username = "client@example.com", roles = "CLIENT")
    void editClientProfile_ValidData_RedirectsToProfile() throws Exception {
        when(clientService.getClientByEmail("client@example.com")).thenReturn(buildClientDTO());

        mockMvc.perform(post("/profile/edit/client").with(csrf())
                        .param("email", "client@example.com")
                        .param("name", "Alice Updated")
                        .param("password", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile?success=updated"));

        verify(clientService).updateClient(eq(1L), any(UpdateClientDTO.class));
    }

    @Test
    @WithMockUser(username = "client@example.com", roles = "CLIENT")
    void editClientProfile_ValidationErrors_ReturnsEditClientView() throws Exception {
        mockMvc.perform(post("/profile/edit/client").with(csrf())
                        .param("email", "not-an-email")
                        .param("name", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/edit-client"));

        verify(clientService, never()).updateClient(anyLong(), any());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void editClientProfile_AsEmployee_IsForbidden() throws Exception {
        mockMvc.perform(post("/profile/edit/client").with(csrf()).param("email", "emp@example.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "client@example.com", roles = "CLIENT")
    void deleteAccount_AsClient_DeletesAndRedirects() throws Exception {
        when(clientService.getClientByEmail("client@example.com")).thenReturn(buildClientDTO());

        mockMvc.perform(post("/profile/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?success=accountDeleted"));

        verify(clientService).deleteClient(1L);
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void deleteAccount_AsEmployee_IsForbidden() throws Exception {
        mockMvc.perform(post("/profile/delete").with(csrf())).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "client@example.com", roles = "CLIENT")
    void topUpBalance_WithReferer_RedirectsToReferer() throws Exception {
        mockMvc.perform(post("/profile/balance/topup").with(csrf())
                        .param("amount", "50.00")
                        .header("Referer", "/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        verify(clientService).topUpBalance("client@example.com", new BigDecimal("50.00"));
    }

    @Test
    @WithMockUser(username = "client@example.com", roles = "CLIENT")
    void topUpBalance_NoReferer_RedirectsToBooks() throws Exception {
        mockMvc.perform(post("/profile/balance/topup").with(csrf()).param("amount", "100.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void topUpBalance_AsEmployee_IsForbidden() throws Exception {
        mockMvc.perform(post("/profile/balance/topup").with(csrf()).param("amount", "50.00"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "emp@example.com", roles = "EMPLOYEE")
    void editEmployeeProfile_ValidData_RedirectsToProfile() throws Exception {
        when(employeeService.getEmployeeByEmail("emp@example.com")).thenReturn(buildEmployeeDTO());

        mockMvc.perform(post("/profile/edit/employee").with(csrf())
                        .param("email", "emp@example.com")
                        .param("name", "Bob Updated")
                        .param("birthDate", "1990-01-01")
                        .param("phone", "+380991234567")
                        .param("password", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile?success=updated"));

        verify(employeeService).updateEmployee(eq(2L), any(UpdateEmployeeDTO.class));
    }

    @Test
    @WithMockUser(username = "emp@example.com", roles = "EMPLOYEE")
    void editEmployeeProfile_ValidationErrors_ReturnsEditEmployeeView() throws Exception {
        mockMvc.perform(post("/profile/edit/employee").with(csrf())
                        .param("name", "").param("email", "bad-email"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/edit-employee"));

        verify(employeeService, never()).updateEmployee(anyLong(), any());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void editEmployeeProfile_AsClient_IsForbidden() throws Exception {
        mockMvc.perform(post("/profile/edit/employee").with(csrf()).param("name", "Test"))
                .andExpect(status().isNotFound());
    }
}




@WebMvcTest(HomeController.class)
class HomeControllerTest extends BaseControllerTest {

    @Autowired MockMvc mockMvc;

    @Test
    @WithMockUser
    void home_Authenticated_RedirectsToBooks() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));
    }

    @Test
    void home_Unauthenticated_AlsoRedirectsToBooks() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));
    }
}