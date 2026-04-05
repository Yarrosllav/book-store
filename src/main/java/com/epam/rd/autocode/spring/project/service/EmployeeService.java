package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.CreateEmployeeDTO;
import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.dto.UpdateEmployeeDTO;

import java.util.List;

public interface EmployeeService {

    List<EmployeeDTO> getAllEmployees();

    EmployeeDTO getEmployeeByEmail(String email);

    EmployeeDTO getEmployeeById(Long id);

    EmployeeDTO updateEmployee(Long id, UpdateEmployeeDTO employee);

    void deleteEmployee(Long id);

    EmployeeDTO addEmployee(CreateEmployeeDTO employee);
}
