package com.epam.rd.autocode.spring.project.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException() {
        super("error.exception.notfound.order");
    }
}
