package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Basket;
import com.epam.rd.autocode.spring.project.model.BasketItem;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.service.BasketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasketServiceImpl implements BasketService {

    private final ClientRepository clientRepository;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public void addBookToBasket(String clientEmail, Long bookId, Integer quantity) {
        log.info("Adding book {} to basket for client {}", bookId, clientEmail);

        Client client = clientRepository.findByEmail(clientEmail)
                .orElseThrow(() -> new NotFoundException("Client not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book not found"));

        Basket basket = client.getBasket();

        Optional<BasketItem> existingItem = basket.getBasketItems().stream()
                .filter(item -> item.getBook().getId().equals(bookId))
                .findFirst();

        if (existingItem.isPresent()) {
            BasketItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        }else{
            BasketItem item = new BasketItem();
            item.setBasket(basket);
            item.setBook(book);
            item.setQuantity(quantity);

            basket.getBasketItems().add(item);
        }

        clientRepository.save(client);
    }
}
