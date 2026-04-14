package com.epam.rd.autocode.spring.project.exception;

import lombok.Getter;

@Getter
public class ClientAlreadyExistsException extends RuntimeException {

    public ClientAlreadyExistsException() {
        super("error.exception.already_exists.client");
    }

}
