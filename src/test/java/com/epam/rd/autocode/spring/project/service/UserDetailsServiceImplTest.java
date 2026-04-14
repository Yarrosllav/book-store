package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.model.enums.Role;
import com.epam.rd.autocode.spring.project.repo.UserRepository;
import com.epam.rd.autocode.spring.project.service.impl.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsername_ExistingEmployee_ReturnsUserDetailsWithRole() {
        Employee employee = new Employee();
        employee.setEmail("emp@example.com");
        employee.setPassword("encoded_pass");
        employee.setRole(Role.EMPLOYEE);

        when(userRepository.findByEmail("emp@example.com")).thenReturn(Optional.of(employee));

        UserDetails result = userDetailsService.loadUserByUsername("emp@example.com");

        assertThat(result.getUsername()).isEqualTo("emp@example.com");
        assertThat(result.getPassword()).isEqualTo("encoded_pass");
        assertThat(result.getAuthorities()).anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"));
        assertThat(result.isAccountNonLocked()).isTrue();
        assertThat(result.isEnabled()).isTrue();
    }

    @Test
    void loadUserByUsername_ActiveClient_IsNonLocked() {
        Client client = new Client();
        client.setEmail("client@example.com");
        client.setPassword("encoded_pass");
        client.setRole(Role.CLIENT);
        client.setIsBlocked(false);

        when(userRepository.findByEmail("client@example.com")).thenReturn(Optional.of(client));

        UserDetails result = userDetailsService.loadUserByUsername("client@example.com");

        assertThat(result.isAccountNonLocked()).isTrue();
        assertThat(result.getAuthorities()).anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"));
    }

    @Test
    void loadUserByUsername_BlockedClient_IsLocked() {
        Client client = new Client();
        client.setEmail("blocked@example.com");
        client.setPassword("encoded_pass");
        client.setRole(Role.CLIENT);
        client.setIsBlocked(true);

        when(userRepository.findByEmail("blocked@example.com")).thenReturn(Optional.of(client));

        UserDetails result = userDetailsService.loadUserByUsername("blocked@example.com");

        assertThat(result.isAccountNonLocked()).isFalse();
    }

    @Test
    void loadUserByUsername_ClientWithNullIsBlocked_IsNonLocked() {
        Client client = new Client();
        client.setEmail("client@example.com");
        client.setPassword("encoded_pass");
        client.setRole(Role.CLIENT);
        client.setIsBlocked(null);

        when(userRepository.findByEmail("client@example.com")).thenReturn(Optional.of(client));

        UserDetails result = userDetailsService.loadUserByUsername("client@example.com");

        assertThat(result.isAccountNonLocked()).isTrue();
    }

//    @Test
//    void loadUserByUsername_AdminUser_HasAdminRole() {
//        Employee admin = new Employee();
//        admin.setEmail("admin@example.com");
//        admin.setPassword("encoded_pass");
//        admin.setRole(Role.ADMIN);
//
//        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));
//
//        UserDetails result = userDetailsService.loadUserByUsername("admin@example.com");
//
//        assertThat(result.getAuthorities()).anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
//    }

    @Test
    void loadUserByUsername_NonExistingUser_ThrowsUsernameNotFoundException() {
        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("nobody@example.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void loadUserByUsername_AccountFlags_AreAllTrueByDefault() {
        Employee employee = new Employee();
        employee.setEmail("emp@example.com");
        employee.setPassword("pass");
        employee.setRole(Role.EMPLOYEE);

        when(userRepository.findByEmail("emp@example.com")).thenReturn(Optional.of(employee));

        UserDetails result = userDetailsService.loadUserByUsername("emp@example.com");

        assertThat(result.isEnabled()).isTrue();
        assertThat(result.isAccountNonExpired()).isTrue();
        assertThat(result.isCredentialsNonExpired()).isTrue();
    }

    @Test
    void loadUserByUsername_OnlyOneAuthorityGranted() {
        Employee employee = new Employee();
        employee.setEmail("emp@example.com");
        employee.setPassword("pass");
        employee.setRole(Role.EMPLOYEE);

        when(userRepository.findByEmail("emp@example.com")).thenReturn(Optional.of(employee));

        UserDetails result = userDetailsService.loadUserByUsername("emp@example.com");

        assertThat(result.getAuthorities()).hasSize(1);
    }
}
