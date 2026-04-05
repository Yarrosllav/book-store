package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.dto.CreateBookDTO;
import com.epam.rd.autocode.spring.project.dto.UpdateBookDTO;

import java.util.List;

public interface BookService {

    List<BookDTO> getAllBooks();

    BookDTO getBookById(Long id);

    BookDTO updateBookById(Long id, UpdateBookDTO book);

    void deleteBook(Long id);

    BookDTO addBook(CreateBookDTO book);
}
