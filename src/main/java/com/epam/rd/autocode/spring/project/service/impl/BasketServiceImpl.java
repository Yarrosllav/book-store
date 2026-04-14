package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BasketDTO;
import com.epam.rd.autocode.spring.project.dto.BasketItemDTO;
import com.epam.rd.autocode.spring.project.exception.BasketItemNotFoundException;
import com.epam.rd.autocode.spring.project.exception.BookNotFoundException;
import com.epam.rd.autocode.spring.project.exception.ClientNotFoundException;
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

import java.math.BigDecimal;
import java.util.List;
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

        Client client = clientRepository.findByEmail(clientEmail)
                .orElseThrow(ClientNotFoundException::new);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(BookNotFoundException::new);

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

        log.info("Client [{}] added {} book(s) with ID={} to basket", clientEmail, quantity, bookId);
    }

    @Override
    @Transactional(readOnly = true)
    public BasketDTO getBasketByClientEmail(String email) {
        Client client = clientRepository.findByEmail(email).orElseThrow(ClientNotFoundException::new);

        Basket basket = client.getBasket();

        List<BasketItemDTO> basketItems = basket.getBasketItems().stream()
                .map(item -> {
                    BigDecimal totalItemPrice = item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                    return BasketItemDTO.builder()
                            .id(item.getId())
                            .bookId(item.getBook().getId())
                            .bookName(item.getBook().getName())
                            .author(item.getBook().getAuthor())
                            .price(item.getBook().getPrice())
                            .quantity(item.getQuantity())
                            .totalItemPrice(totalItemPrice)
                            .build();

                }).toList();

        BigDecimal totalPrice = basketItems.stream()
                .map(BasketItemDTO::getTotalItemPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new BasketDTO(basketItems, totalPrice);
    }

    @Override
    @Transactional
    public void removeBookFromBasket(String clientEmail, Long basketItemId) {
        Client client = clientRepository.findByEmail(clientEmail).orElseThrow(ClientNotFoundException::new);

        client.getBasket().getBasketItems().removeIf(item -> item.getId().equals(basketItemId));

        clientRepository.save(client);

        log.info("Client [{}] removed item ID={} from basket", clientEmail, basketItemId);
    }

    @Override
    @Transactional
    public void updateQuantity(String clientEmail, Long basketItemId, Integer delta) {

        Client client = clientRepository.findByEmail(clientEmail).orElseThrow(ClientNotFoundException::new);

        BasketItem item = client.getBasket().getBasketItems().stream()
                .filter(i -> i.getId().equals(basketItemId))
                .findFirst()
                .orElseThrow(BasketItemNotFoundException::new);

        if((item.getQuantity() + delta) == 0) return;

        int newQuantity = item.getQuantity() + delta;

        item.setQuantity(newQuantity);

        clientRepository.save(client);

        log.info("Client [{}] updated quantity for item ID={} (delta: {})", clientEmail, basketItemId, delta);
    }
}
