package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.dto.UpdateEmployeeDTO;
import com.epam.rd.autocode.spring.project.exception.EmployeeNotFoundException;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ModelMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream().map(e -> mapper.map(e, EmployeeDTO.class)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeDTO getEmployeeByEmail(String email) {

        return employeeRepository.findByEmail(email)
                .map(e -> mapper.map(e, EmployeeDTO.class))
                .orElseThrow(EmployeeNotFoundException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeDTO getEmployeeById(Long id) {

        return employeeRepository.findById(id)
                .map(e -> mapper.map(e, EmployeeDTO.class))
                .orElseThrow(EmployeeNotFoundException::new);
    }

    @Override
    @Transactional
    public EmployeeDTO updateEmployee(Long id, UpdateEmployeeDTO employeeDTO) {
        Employee employee = employeeRepository.findById(id).orElseThrow(EmployeeNotFoundException::new);

        if (employeeDTO.getName() != null) {
            employee.setName(employeeDTO.getName());
        }
        if (employeeDTO.getBirthDate() != null) {
            employee.setBirthDate(employeeDTO.getBirthDate());
        }
        if (employeeDTO.getPhone() != null) {
            employee.setPhone(employeeDTO.getPhone());
        }

        if(employeeDTO.getPassword() != null && !employeeDTO.getPassword().isBlank()) {
            employee.setPassword(passwordEncoder.encode(employeeDTO.getPassword()));
        }

        log.info("Employee ID={} profile successfully updated", id);
        return mapper.map(employee, EmployeeDTO.class);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(EmployeeNotFoundException::new);

        employeeRepository.delete(employee);
        log.warn("Employee ID={} has been permanently deleted", id);
    }
}
