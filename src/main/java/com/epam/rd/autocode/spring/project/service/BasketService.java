package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.BasketDTO;

public interface BasketService {

    void addBookToBasket(String clientEmail, Long bookId, Integer quantity);
    BasketDTO getBasketByClientEmail(String email);
    void removeBookFromBasket(String clientEmail, Long basketItemId);
    void updateQuantity(String clientEmail, Long basketItemId, Integer quantity);

}
