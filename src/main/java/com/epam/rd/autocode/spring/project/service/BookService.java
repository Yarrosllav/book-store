package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.dto.BookFilterDTO;
import org.springframework.data.domain.Page;

public interface BookService {

    Page<BookDTO> getAllBooks(BookFilterDTO filter, int page, int size, String sortBy, String sortDir);

    BookDTO getBookById(Long id);

    BookDTO updateBookById(Long id, BookDTO book);

    void archiveBook(Long id);

    BookDTO addBook(BookDTO book);

}
