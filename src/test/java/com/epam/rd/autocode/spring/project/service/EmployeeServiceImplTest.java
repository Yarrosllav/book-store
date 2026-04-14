package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.dto.UpdateEmployeeDTO;
import com.epam.rd.autocode.spring.project.exception.EmployeeNotFoundException;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock private EmployeeRepository employeeRepository;
    @Mock private ModelMapper mapper;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private EmployeeDTO employeeDTO;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setEmail("emp@example.com");
        employee.setName("Alice");
        employee.setPhone("+380991234567");
        employee.setBirthDate(LocalDate.of(1990, 1, 1));
        employee.setPassword("encoded_pass");

        employeeDTO = new EmployeeDTO();
        employeeDTO.setId(1L);
        employeeDTO.setEmail("emp@example.com");
        employeeDTO.setName("Alice");
    }

    @Test
    void getAllEmployees_ReturnsListOfDTOs() {
        when(employeeRepository.findAll()).thenReturn(List.of(employee));
        when(mapper.map(employee, EmployeeDTO.class)).thenReturn(employeeDTO);

        List<EmployeeDTO> result = employeeService.getAllEmployees();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("emp@example.com");
    }

    @Test
    void getAllEmployees_EmptyList_ReturnsEmptyList() {
        when(employeeRepository.findAll()).thenReturn(List.of());

        List<EmployeeDTO> result = employeeService.getAllEmployees();

        assertThat(result).isEmpty();
    }

    @Test
    void getEmployeeByEmail_ExistingEmail_ReturnsDTO() {
        when(employeeRepository.findByEmail("emp@example.com")).thenReturn(Optional.of(employee));
        when(mapper.map(employee, EmployeeDTO.class)).thenReturn(employeeDTO);

        EmployeeDTO result = employeeService.getEmployeeByEmail("emp@example.com");

        assertThat(result.getEmail()).isEqualTo("emp@example.com");
    }

    @Test
    void getEmployeeByEmail_NonExistingEmail_ThrowsEmployeeNotFoundException() {
        when(employeeRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getEmployeeByEmail("nobody@example.com"))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void getEmployeeById_ExistingId_ReturnsDTO() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(mapper.map(employee, EmployeeDTO.class)).thenReturn(employeeDTO);

        EmployeeDTO result = employeeService.getEmployeeById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getEmployeeById_NonExistingId_ThrowsEmployeeNotFoundException() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getEmployeeById(99L))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void updateEmployee_AllFieldsProvided_UpdatesAllFields() {
        UpdateEmployeeDTO dto = new UpdateEmployeeDTO();
        dto.setName("Bob");
        dto.setBirthDate(LocalDate.of(1995, 5, 20));
        dto.setPhone("+380991111111");
        dto.setPassword("newPass123");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(passwordEncoder.encode("newPass123")).thenReturn("encoded_new");
        when(mapper.map(employee, EmployeeDTO.class)).thenReturn(employeeDTO);

        employeeService.updateEmployee(1L, dto);

        assertThat(employee.getName()).isEqualTo("Bob");
        assertThat(employee.getBirthDate()).isEqualTo(LocalDate.of(1995, 5, 20));
        assertThat(employee.getPhone()).isEqualTo("+380991111111");
        assertThat(employee.getPassword()).isEqualTo("encoded_new");
    }

    @Test
    void updateEmployee_NullFields_DoesNotOverrideExistingValues() {
        UpdateEmployeeDTO dto = new UpdateEmployeeDTO();
        dto.setName(null);
        dto.setBirthDate(null);
        dto.setPhone(null);
        dto.setPassword(null);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(mapper.map(employee, EmployeeDTO.class)).thenReturn(employeeDTO);

        employeeService.updateEmployee(1L, dto);

        assertThat(employee.getName()).isEqualTo("Alice");
        assertThat(employee.getPhone()).isEqualTo("+380991234567");
        assertThat(employee.getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(employee.getPassword()).isEqualTo("encoded_pass");
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void updateEmployee_BlankPassword_DoesNotEncodeOrChangePassword() {
        UpdateEmployeeDTO dto = new UpdateEmployeeDTO();
        dto.setPassword("   ");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(mapper.map(employee, EmployeeDTO.class)).thenReturn(employeeDTO);

        employeeService.updateEmployee(1L, dto);

        assertThat(employee.getPassword()).isEqualTo("encoded_pass");
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void updateEmployee_EmployeeNotFound_ThrowsEmployeeNotFoundException() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.updateEmployee(99L, new UpdateEmployeeDTO()))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void deleteEmployee_ExistingEmployee_DeletesSuccessfully() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        employeeService.deleteEmployee(1L);

        verify(employeeRepository).delete(employee);
    }

    @Test
    void deleteEmployee_NonExistingEmployee_ThrowsEmployeeNotFoundException() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.deleteEmployee(99L))
                .isInstanceOf(EmployeeNotFoundException.class);

        verify(employeeRepository, never()).delete(any());
    }
}
