package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.dto.BookFilterDTO;
import com.epam.rd.autocode.spring.project.exception.BookAlreadyExistsException;
import com.epam.rd.autocode.spring.project.exception.BookNotFoundException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;
    private BookDTO bookDTO;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setName("Clean Code");
        book.setAuthor("Robert Martin");
        book.setPrice(new BigDecimal("250.00"));
        book.setIsAvailable(true);

        bookDTO = new BookDTO();
        bookDTO.setName("Clean Code");
        bookDTO.setAuthor("Robert Martin");
        bookDTO.setPrice(new BigDecimal("250.00"));
    }

    @Test
    void getAllBooks_ReturnsPageOfBookDTOs() {
        BookFilterDTO filter = new BookFilterDTO();
        filter.setSearch("Clean");

        Page<Book> bookPage = new PageImpl<>(List.of(book));
        when(bookRepository.findWithFilters(eq("Clean"), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(bookPage);
        when(mapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        Page<BookDTO> result = bookService.getAllBooks(filter, 0, 10, "name", "asc");

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Clean Code");
    }

    @Test
    void getAllBooks_BlankSearch_PassesNullToRepository() {
        BookFilterDTO filter = new BookFilterDTO();
        filter.setSearch("   ");

        Page<Book> emptyPage = new PageImpl<>(List.of());
        when(bookRepository.findWithFilters(isNull(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(emptyPage);

        Page<BookDTO> result = bookService.getAllBooks(filter, 0, 10, "name", "desc");

        assertThat(result.getContent()).isEmpty();
        verify(bookRepository).findWithFilters(isNull(), any(), any(), any(), any(), any(Pageable.class));
    }

    @Test
    void getAllBooks_DescendingSort_CreatesSortCorrectly() {
        BookFilterDTO filter = new BookFilterDTO();

        Page<Book> page = new PageImpl<>(List.of());
        when(bookRepository.findWithFilters(any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(page);

        bookService.getAllBooks(filter, 0, 5, "price", "desc");

        verify(bookRepository).findWithFilters(
                any(), any(), any(), any(), any(),
                argThat(pageable -> pageable.getSort().getOrderFor("price").getDirection() == Sort.Direction.DESC)
        );
    }

    @Test
    void getBookById_ExistingId_ReturnsBookDTO() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(mapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        BookDTO result = bookService.getBookById(1L);

        assertThat(result.getName()).isEqualTo("Clean Code");
    }

    @Test
    void getBookById_NonExistingId_ThrowsBookNotFoundException() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBookById(99L))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void updateBookById_ValidUpdate_ReturnsUpdatedBookDTO() {
        BookDTO updateDTO = new BookDTO();
        updateDTO.setName("Updated Name");
        updateDTO.setAuthor("New Author");
        updateDTO.setPrice(new BigDecimal("300.00"));

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.findByName("Updated Name")).thenReturn(Optional.empty());
        when(mapper.map(book, BookDTO.class)).thenReturn(updateDTO);

        BookDTO result = bookService.updateBookById(1L, updateDTO);

        assertThat(book.getName()).isEqualTo("Updated Name");
        assertThat(book.getAuthor()).isEqualTo("New Author");
        assertThat(book.getPrice()).isEqualByComparingTo("300.00");
    }

    @Test
    void updateBookById_SameNameDifferentIdDifferentAuthor_DoesNotThrow() {
        BookDTO updateDTO = new BookDTO();
        updateDTO.setName("Clean Code");
        updateDTO.setAuthor("Different Author");

        Book conflicting = new Book();
        conflicting.setId(99L);
        conflicting.setName("Clean Code");
        conflicting.setAuthor("Robert Martin");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.findByName("Clean Code")).thenReturn(Optional.of(conflicting));
        when(mapper.map(book, BookDTO.class)).thenReturn(updateDTO);

        assertThatCode(() -> bookService.updateBookById(1L, updateDTO))
                .doesNotThrowAnyException();
    }

    @Test
    void updateBookById_SameBookSameAuthorDifferentId_ThrowsBookAlreadyExistsException() {
        BookDTO updateDTO = new BookDTO();
        updateDTO.setName("Clean Code");
        updateDTO.setAuthor("Robert Martin");

        Book conflicting = new Book();
        conflicting.setId(99L);
        conflicting.setName("Clean Code");
        conflicting.setAuthor("Robert Martin");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.findByName("Clean Code")).thenReturn(Optional.of(conflicting));

        assertThatThrownBy(() -> bookService.updateBookById(1L, updateDTO))
                .isInstanceOf(BookAlreadyExistsException.class);
    }

    @Test
    void updateBookById_NameConflictButSameId_DoesNotThrow() {
        BookDTO updateDTO = new BookDTO();
        updateDTO.setName("Clean Code");
        updateDTO.setAuthor("Robert Martin");
        updateDTO.setPrice(new BigDecimal("290.00"));

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.findByName("Clean Code")).thenReturn(Optional.of(book));
        when(mapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        assertThatCode(() -> bookService.updateBookById(1L, updateDTO))
                .doesNotThrowAnyException();
    }

    @Test
    void updateBookById_BookNotFound_ThrowsBookNotFoundException() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.updateBookById(99L, bookDTO))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void archiveBook_ExistingBook_SetsIsAvailableToFalse() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.archiveBook(1L);

        assertThat(book.getIsAvailable()).isFalse();
        verify(bookRepository).save(book);
    }

    @Test
    void archiveBook_NonExistingBook_ThrowsBookNotFoundException() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.archiveBook(99L))
                .isInstanceOf(BookNotFoundException.class);

        verify(bookRepository, never()).save(any());
    }

    @Test
    void addBook_NewBook_SavesAndReturnsDTO() {
        when(bookRepository.findByName("Clean Code")).thenReturn(Optional.empty());
        when(mapper.map(bookDTO, Book.class)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(mapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        BookDTO result = bookService.addBook(bookDTO);

        assertThat(result.getName()).isEqualTo("Clean Code");
        verify(bookRepository).save(book);
    }

    @Test
    void addBook_DuplicateNameAndAuthor_ThrowsBookAlreadyExistsException() {
        when(bookRepository.findByName("Clean Code")).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> bookService.addBook(bookDTO))
                .isInstanceOf(BookAlreadyExistsException.class);

        verify(bookRepository, never()).save(any());
    }

    @Test
    void addBook_SameNameDifferentAuthor_SavesSuccessfully() {
        BookDTO newBook = new BookDTO();
        newBook.setName("Clean Code");
        newBook.setAuthor("Another Author");

        Book differentAuthorBook = new Book();
        differentAuthorBook.setId(2L);
        differentAuthorBook.setName("Clean Code");
        differentAuthorBook.setAuthor("Another Author");

        when(bookRepository.findByName("Clean Code")).thenReturn(Optional.of(book)); // book.author = "Robert Martin"
        when(mapper.map(newBook, Book.class)).thenReturn(differentAuthorBook);
        when(bookRepository.save(differentAuthorBook)).thenReturn(differentAuthorBook);
        when(mapper.map(differentAuthorBook, BookDTO.class)).thenReturn(newBook);

        assertThatCode(() -> bookService.addBook(newBook)).doesNotThrowAnyException();
        verify(bookRepository).save(differentAuthorBook);
    }
}
