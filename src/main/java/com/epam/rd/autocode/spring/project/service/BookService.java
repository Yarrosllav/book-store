package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.dto.BookFilterDTO;
import com.epam.rd.autocode.spring.project.dto.CreateBookDTO;
import com.epam.rd.autocode.spring.project.dto.UpdateBookDTO;
import org.springframework.data.domain.Page;

public interface BookService {

    Page<BookDTO> getAllBooks(BookFilterDTO filter, int page, int size, String sortBy, String sortDir);

    BookDTO getBookById(Long id);

    BookDTO updateBookById(Long id, UpdateBookDTO book);

    void deleteBook(Long id);

    BookDTO addBook(CreateBookDTO book);
}
