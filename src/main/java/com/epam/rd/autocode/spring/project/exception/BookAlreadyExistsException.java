package com.epam.rd.autocode.spring.project.exception;

import lombok.Getter;

@Getter
public class BookAlreadyExistsException extends RuntimeException {

    private final Object[] args;

    public BookAlreadyExistsException(Object... args) {
        super("error.exception.already_exists.book");
        this.args = args;
    }

}
