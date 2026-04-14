package com.epam.rd.autocode.spring.project.exception;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(String message) {
        super(message);
    }

    public ClientNotFoundException() {
        super("error.exception.notfound.client");
    }

}
