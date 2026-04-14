package com.epam.rd.autocode.spring.project.exception;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException() {
        super("error.exception.notfound.employee");
    }
}
