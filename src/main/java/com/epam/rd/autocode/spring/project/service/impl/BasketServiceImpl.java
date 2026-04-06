package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BasketDTO;
import com.epam.rd.autocode.spring.project.dto.BasketItemDTO;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Basket;
import com.epam.rd.autocode.spring.project.model.BasketItem;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.repo.BasketItemRepository;
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
    private final BasketItemRepository basketItemRepository;

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

    @Override
    @Transactional(readOnly = true)
    public BasketDTO getBasketByClientEmail(String email) {
        log.info("Getting basket for client {}", email);
        Client client = clientRepository.findByEmail(email).orElseThrow(NotFoundException::new);

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
        log.info("Removing book {} from basket for client {}", basketItemId, clientEmail);

        Client client = clientRepository.findByEmail(clientEmail).orElseThrow(NotFoundException::new);

        client.getBasket().getBasketItems().removeIf(item -> item.getId().equals(basketItemId));

        clientRepository.save(client);

    }
}
