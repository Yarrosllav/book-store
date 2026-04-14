package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.dto.BookFilterDTO;
import com.epam.rd.autocode.spring.project.exception.BookAlreadyExistsException;
import com.epam.rd.autocode.spring.project.exception.BookNotFoundException;
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
        return mapper.map(bookRepository.findById(id).orElseThrow(BookNotFoundException::new), BookDTO.class);
    }

    @Override
    @Transactional
    public BookDTO updateBookById(Long id, BookDTO bookDTO) {
        Book bookToUpdate = bookRepository.findById(id).orElseThrow(BookNotFoundException::new);

        bookRepository.findByName(bookDTO.getName()).ifPresent(existingBook -> {
            if (!existingBook.getId().equals(id) && existingBook.getAuthor().equals(bookDTO.getAuthor())) {
                throw new BookAlreadyExistsException(bookDTO.getName(), bookDTO.getAuthor());
            }
        });

        bookToUpdate.setName(bookDTO.getName());
        bookToUpdate.setAuthor(bookDTO.getAuthor());
        bookToUpdate.setGenre(bookDTO.getGenre());
        bookToUpdate.setPrice(bookDTO.getPrice());
        bookToUpdate.setAgeGroup(bookDTO.getAgeGroup());
        bookToUpdate.setLanguage(bookDTO.getLanguage());
        bookToUpdate.setPages(bookDTO.getPages());
        bookToUpdate.setPublicationYear(bookDTO.getPublicationYear());
        bookToUpdate.setDescription(bookDTO.getDescription());

        log.info("Book ID={} successfully updated. New title: '{}'", id, bookDTO.getName());
        return mapper.map(bookToUpdate, BookDTO.class);
    }

    @Override
    @Transactional
    public void archiveBook(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(BookNotFoundException::new);
        book.setIsAvailable(false);

        bookRepository.save(book);
        log.info("Book ID={} has been archived (Soft Delete)", id);
    }

    @Override
    @Transactional
    public BookDTO addBook(BookDTO bookDTO) {
        bookRepository.findByName(bookDTO.getName()).ifPresent(book -> {
            if (book.getAuthor().equals(bookDTO.getAuthor())) {
                throw new BookAlreadyExistsException(bookDTO.getName(), bookDTO.getAuthor());
            }
        });

        Book book = mapper.map(bookDTO, Book.class);

        Book savedBook = bookRepository.save(book);

        log.info("New book successfully added: '{}' (Assigned ID={})", savedBook.getName(), savedBook.getId());
        return mapper.map(savedBook, BookDTO.class);
    }

}
