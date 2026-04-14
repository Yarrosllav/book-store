package com.epam.rd.autocode.spring.project.exception;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(String message) {
        super(message);
    }

    public BookNotFoundException() {
        super("error.exception.notfound.book");
    }

}
