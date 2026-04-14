package com.epam.rd.autocode.spring.project.service;

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
import com.epam.rd.autocode.spring.project.service.impl.BasketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasketServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BasketServiceImpl basketService;

    private Client client;
    private Book book;
    private Basket basket;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setName("Clean Code");
        book.setAuthor("Robert Martin");
        book.setPrice(new BigDecimal("250.00"));

        basket = new Basket();
        basket.setBasketItems(new ArrayList<>());

        client = new Client();
        client.setId(1L);
        client.setEmail("user@example.com");
        client.setBasket(basket);
        basket.setClient(client);
    }


    @Test
    void addBookToBasket_NewBook_AddsNewBasketItem() {
        when(clientRepository.findByEmail("user@example.com")).thenReturn(Optional.of(client));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        basketService.addBookToBasket("user@example.com", 1L, 2);

        assertThat(basket.getBasketItems()).hasSize(1);
        assertThat(basket.getBasketItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(basket.getBasketItems().get(0).getBook()).isEqualTo(book);
        verify(clientRepository).save(client);
    }

    @Test
    void addBookToBasket_ExistingBook_IncreasesQuantity() {
        BasketItem existingItem = new BasketItem();
        existingItem.setBook(book);
        existingItem.setQuantity(3);
        basket.getBasketItems().add(existingItem);

        when(clientRepository.findByEmail("user@example.com")).thenReturn(Optional.of(client));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        basketService.addBookToBasket("user@example.com", 1L, 2);

        assertThat(basket.getBasketItems()).hasSize(1);
        assertThat(basket.getBasketItems().get(0).getQuantity()).isEqualTo(5);
        verify(clientRepository).save(client);
    }

    @Test
    void addBookToBasket_ClientNotFound_ThrowsClientNotFoundException() {
        when(clientRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> basketService.addBookToBasket("unknown@example.com", 1L, 1))
                .isInstanceOf(ClientNotFoundException.class);

        verify(bookRepository, never()).findById(any());
        verify(clientRepository, never()).save(any());
    }

    @Test
    void addBookToBasket_BookNotFound_ThrowsBookNotFoundException() {
        when(clientRepository.findByEmail("user@example.com")).thenReturn(Optional.of(client));
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> basketService.addBookToBasket("user@example.com", 99L, 1))
                .isInstanceOf(BookNotFoundException.class);

        verify(clientRepository, never()).save(any());
    }


    @Test
    void getBasketByClientEmail_EmptyBasket_ReturnsDTOWithZeroTotal() {
        when(clientRepository.findByEmail("user@example.com")).thenReturn(Optional.of(client));

        BasketDTO result = basketService.getBasketByClientEmail("user@example.com");

        assertThat(result.getItems()).isEmpty();
        assertThat(result.getTotalPrice()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void getBasketByClientEmail_WithItems_ReturnsDTOWithCorrectTotals() {
        BasketItem item = new BasketItem();
        item.setId(10L);
        item.setBook(book);
        item.setQuantity(3);
        item.setBasket(basket);
        basket.getBasketItems().add(item);

        when(clientRepository.findByEmail("user@example.com")).thenReturn(Optional.of(client));

        BasketDTO result = basketService.getBasketByClientEmail("user@example.com");

        assertThat(result.getItems()).hasSize(1);
        BasketItemDTO itemDTO = result.getItems().get(0);
        assertThat(itemDTO.getBookId()).isEqualTo(1L);
        assertThat(itemDTO.getBookName()).isEqualTo("Clean Code");
        assertThat(itemDTO.getQuantity()).isEqualTo(3);
        assertThat(itemDTO.getPrice()).isEqualByComparingTo("250.00");
        assertThat(itemDTO.getTotalItemPrice()).isEqualByComparingTo("750.00");
        assertThat(result.getTotalPrice()).isEqualByComparingTo("750.00");
    }

    @Test
    void getBasketByClientEmail_MultipleItems_SumsTotalPriceCorrectly() {
        Book book2 = new Book();
        book2.setId(2L);
        book2.setName("Refactoring");
        book2.setAuthor("Fowler");
        book2.setPrice(new BigDecimal("300.00"));

        BasketItem item1 = new BasketItem();
        item1.setId(1L);
        item1.setBook(book);
        item1.setQuantity(2);

        BasketItem item2 = new BasketItem();
        item2.setId(2L);
        item2.setBook(book2);
        item2.setQuantity(1);

        basket.getBasketItems().addAll(List.of(item1, item2));

        when(clientRepository.findByEmail("user@example.com")).thenReturn(Optional.of(client));

        BasketDTO result = basketService.getBasketByClientEmail("user@example.com");

        assertThat(result.getTotalPrice()).isEqualByComparingTo("800.00");
    }

    @Test
    void getBasketByClientEmail_ClientNotFound_ThrowsClientNotFoundException() {
        when(clientRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> basketService.getBasketByClientEmail("nobody@example.com"))
                .isInstanceOf(ClientNotFoundException.class);
    }


    @Test
    void removeBookFromBasket_ExistingItem_RemovesItem() {
        BasketItem item = new BasketItem();
        item.setId(5L);
        item.setBook(book);
        item.setQuantity(1);
        basket.getBasketItems().add(item);

        when(clientRepository.findByEmail("user@example.com")).thenReturn(Optional.of(client));

        basketService.removeBookFromBasket("user@example.com", 5L);

        assertThat(basket.getBasketItems()).isEmpty();
        verify(clientRepository).save(client);
    }

    @Test
    void removeBookFromBasket_NonExistingItemId_DoesNotRemoveAnything() {
        BasketItem item = new BasketItem();
        item.setId(5L);
        item.setBook(book);
        item.setQuantity(1);
        basket.getBasketItems().add(item);

        when(clientRepository.findByEmail("user@example.com")).thenReturn(Optional.of(client));

        basketService.removeBookFromBasket("user@example.com", 999L);

        assertThat(basket.getBasketItems()).hasSize(1);
        verify(clientRepository).save(client);
    }

    @Test
    void removeBookFromBasket_ClientNotFound_ThrowsClientNotFoundException() {
        when(clientRepository.findByEmail("bad@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> basketService.removeBookFromBasket("bad@example.com", 1L))
                .isInstanceOf(ClientNotFoundException.class);

        verify(clientRepository, never()).save(any());
    }


    @Test
    void updateQuantity_PositiveDelta_IncreasesQuantity() {
        BasketItem item = new BasketItem();
        item.setId(1L);
        item.setBook(book);
        item.setQuantity(2);
        basket.getBasketItems().add(item);

        when(clientRepository.findByEmail("user@example.com")).thenReturn(Optional.of(client));

        basketService.updateQuantity("user@example.com", 1L, 3);

        assertThat(item.getQuantity()).isEqualTo(5);
        verify(clientRepository).save(client);
    }

    @Test
    void updateQuantity_NegativeDelta_DecreasesQuantity() {
        BasketItem item = new BasketItem();
        item.setId(1L);
        item.setBook(book);
        item.setQuantity(5);
        basket.getBasketItems().add(item);

        when(clientRepository.findByEmail("user@example.com")).thenReturn(Optional.of(client));

        basketService.updateQuantity("user@example.com", 1L, -2);

        assertThat(item.getQuantity()).isEqualTo(3);
        verify(clientRepository).save(client);
    }

    @Test
    void updateQuantity_DeltaResultsInZero_DoesNotSave() {
        BasketItem item = new BasketItem();
        item.setId(1L);
        item.setBook(book);
        item.setQuantity(2);
        basket.getBasketItems().add(item);

        when(clientRepository.findByEmail("user@example.com")).thenReturn(Optional.of(client));

        basketService.updateQuantity("user@example.com", 1L, -2);

        verify(clientRepository, never()).save(any());
    }

    @Test
    void updateQuantity_ItemNotFound_ThrowsBasketItemNotFoundException() {
        when(clientRepository.findByEmail("user@example.com")).thenReturn(Optional.of(client));

        assertThatThrownBy(() -> basketService.updateQuantity("user@example.com", 999L, 1))
                .isInstanceOf(BasketItemNotFoundException.class);
    }

    @Test
    void updateQuantity_ClientNotFound_ThrowsClientNotFoundException() {
        when(clientRepository.findByEmail("none@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> basketService.updateQuantity("none@example.com", 1L, 1))
                .isInstanceOf(ClientNotFoundException.class);
    }
}
