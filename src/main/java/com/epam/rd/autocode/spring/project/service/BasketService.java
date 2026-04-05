package com.epam.rd.autocode.spring.project.service;

public interface BasketService {

    void addBookToBasket(String clientEmail, Long bookId, Integer quantity);

}
