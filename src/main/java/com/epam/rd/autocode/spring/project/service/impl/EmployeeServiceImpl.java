package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.CreateEmployeeDTO;
import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.dto.UpdateEmployeeDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.model.enums.Role;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.repo.UserRepository;
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
    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDTO> getAllEmployees() {
        log.info("Getting all employees");
        return employeeRepository.findAll().stream().map(e -> mapper.map(e, EmployeeDTO.class)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeDTO getEmployeeByEmail(String email) {
        log.info("Getting employee by email: {}", email);

        return employeeRepository.findByEmail(email)
                .map(e -> mapper.map(e, EmployeeDTO.class))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeDTO getEmployeeById(Long id) {
        log.info("Getting employee by id: {}", id);

        return employeeRepository.findById(id)
                .map(e -> mapper.map(e, EmployeeDTO.class))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    @Transactional
    public EmployeeDTO updateEmployee(Long id, UpdateEmployeeDTO employeeDTO) {
        log.info("Updating employee by id: {}", id);

        Employee employee = employeeRepository.findById(id).orElseThrow(NotFoundException::new);

        if(employeeDTO.getEmail() != null && !employee.getEmail().equals(employeeDTO.getEmail()) && userRepository.existsByEmail(employeeDTO.getEmail())) {
            throw new AlreadyExistException("Email " + employeeDTO.getEmail() + " already exists");
        }

        if (employeeDTO.getEmail() != null) {
            employee.setEmail(employeeDTO.getEmail());
        }
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


        return mapper.map(employee, EmployeeDTO.class);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {

        log.info("Deleting employee by id: {}", id);

        Employee employee = employeeRepository.findById(id).orElseThrow(NotFoundException::new);

        employeeRepository.delete(employee);

    }

    @Override
    @Transactional
    public EmployeeDTO addEmployee(CreateEmployeeDTO employeeDTO) {
        log.info("Adding employee with email: {}", employeeDTO.getEmail());

        if(userRepository.existsByEmail(employeeDTO.getEmail())) {
            throw new AlreadyExistException("User with email " + employeeDTO.getEmail() + " already exists");
        }

        Employee employee = mapper.map(employeeDTO, Employee.class);

        String encodedPassword = passwordEncoder.encode(employeeDTO.getPassword());
        employee.setPassword(encodedPassword);
        employee.setRole(Role.EMPLOYEE);

        Employee savedEmployee = employeeRepository.save(employee);

        return mapper.map(savedEmployee, EmployeeDTO.class);
    }
}
