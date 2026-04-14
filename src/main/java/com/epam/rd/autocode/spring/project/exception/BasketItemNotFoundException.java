package com.epam.rd.autocode.spring.project.exception;

public class BasketItemNotFoundException extends RuntimeException {
    public BasketItemNotFoundException() {
        super("error.exception.notfound.basket_item");
    }
}
