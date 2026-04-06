package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.dto.BookFilterDTO;
import com.epam.rd.autocode.spring.project.dto.CreateBookDTO;
import com.epam.rd.autocode.spring.project.dto.UpdateBookDTO;
import com.epam.rd.autocode.spring.project.exception.DuplicateBookException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final ModelMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<BookDTO> getAllBooks(BookFilterDTO filter, int page, int size, String sortBy, String sortDir) {
        log.info("Getting all books");

        Sort sort = sortDir.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        String search = (filter.getSearch() == null || filter.getSearch().isBlank()) ? null : filter.getSearch();


        return bookRepository.findWithFilters(search, filter.getGenre(),
                filter.getAgeGroup(), filter.getMinPrice(), filter.getMaxPrice(), pageable)
                .map(book -> mapper.map(book, BookDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public BookDTO getBookById(Long id) {
        log.info("Getting book by id: {}", id);
        return mapper.map(bookRepository.findById(id).orElseThrow(NotFoundException::new), BookDTO.class);
    }

    @Override
    @Transactional
    public BookDTO updateBookById(Long id, UpdateBookDTO book) {

        log.info("Updating book by id: {}", id);

        Book bookToUpdate = bookRepository.findById(id).orElseThrow(NotFoundException::new);

        if (book.getName() != null) {
            bookToUpdate.setName(book.getName());
        }
        if (book.getCharacteristics() != null) {
            bookToUpdate.setCharacteristics(book.getCharacteristics());
        }
        if (book.getPublicationDate() != null) {
            bookToUpdate.setPublicationDate(book.getPublicationDate());
        }
        if (book.getGenre() != null) {
            bookToUpdate.setGenre(book.getGenre());
        }
        if (book.getDescription() != null) {
            bookToUpdate.setDescription(book.getDescription());
        }
        if (book.getPages() != null) {
            bookToUpdate.setPages(book.getPages());
        }
        if (book.getAuthor() != null) {
            bookToUpdate.setAuthor(book.getAuthor());
        }
        if (book.getPrice() != null) {
            bookToUpdate.setPrice(book.getPrice());
        }
        if (book.getLanguage() != null) {
            bookToUpdate.setLanguage(book.getLanguage());
        }
        if (book.getAgeGroup() != null) {
            bookToUpdate.setAgeGroup(book.getAgeGroup());
        }

        return mapper.map(bookToUpdate, BookDTO.class);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        log.info("Deleting book by id: {}", id);

        Book book = bookRepository.findById(id).orElseThrow(NotFoundException::new);

        bookRepository.delete(book);
    }

    @Override
    @Transactional
    public BookDTO addBook(CreateBookDTO bookDTO) {

        log.info("Adding book with name: {}", bookDTO.getName());

        if(bookRepository.existsByName(bookDTO.getName())) {
            throw new DuplicateBookException("Book with name " + bookDTO.getName() + " already exists");
        }

        Book book = mapper.map(bookDTO, Book.class);

        Book savedBook = bookRepository.save(book);

        return mapper.map(savedBook, BookDTO.class);
    }
}
