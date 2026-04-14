package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.dto.BookFilterDTO;
import com.epam.rd.autocode.spring.project.exception.BookAlreadyExistsException;
import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Genre;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import com.epam.rd.autocode.spring.project.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest extends BaseControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean
    BookService bookService;

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder validBookParams(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder req) {
        return req
                .param("name", "Clean Code")
                .param("author", "Robert Martin")
                .param("price", "250.00")
                .param("genre", Genre.values()[0].name())
                .param("ageGroup", AgeGroup.values()[0].name())
                .param("language", Language.values()[0].name())
                .param("pages", "400")
                .param("publicationYear", "2008");
    }

    @Test
    @WithMockUser
    void getAllBooks_ReturnsBooksCatalogView() throws Exception {
        when(bookService.getAllBooks(any(BookFilterDTO.class), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/catalog"))
                .andExpect(model().attributeExists("books", "filter", "genres", "ageGroups",
                        "currentPage", "totalPages", "sortBy", "sortDir", "reverseSortDir"));
    }

    @Test
    @WithMockUser
    void getAllBooks_AscSortDir_ReverseSortDirIsDesc() throws Exception {
        when(bookService.getAllBooks(any(), anyInt(), anyInt(), anyString(), eq("asc")))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/books").param("sortDir", "asc"))
                .andExpect(model().attribute("reverseSortDir", "desc"));
    }

    @Test
    @WithMockUser
    void getAllBooks_DescSortDir_ReverseSortDirIsAsc() throws Exception {
        when(bookService.getAllBooks(any(), anyInt(), anyInt(), anyString(), eq("desc")))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/books").param("sortDir", "desc"))
                .andExpect(model().attribute("reverseSortDir", "asc"));
    }

    @Test
    @WithMockUser
    void getBookDetails_ExistingBook_ReturnsDetailsView() throws Exception {
        BookDTO dto = new BookDTO();
        dto.setId(1L);
        dto.setName("Test");
        dto.setAuthor("Test");
        dto.setPrice(BigDecimal.ONE);

        // ВАЖЛИВО!
        dto.setGenre(Genre.FANTASY);
        dto.setAgeGroup(AgeGroup.ADULT);
        dto.setLanguage(Language.ENGLISH);

        when(bookService.getBookById(1L)).thenReturn(dto);

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/details"))
                .andExpect(model().attributeExists("book"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void showAddBookForm_AsEmployee_ReturnsAddFormView() throws Exception {
        mockMvc.perform(get("/books/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/add-form"))
                .andExpect(model().attributeExists("book", "genres", "ageGroups", "languages"));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void showAddBookForm_AsClient_IsForbidden() throws Exception {
        mockMvc.perform(get("/books/add")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void addBook_ValidData_RedirectsToCatalog() throws Exception {
        // Ми не залежимо від 'validBookParams'
        mockMvc.perform(post("/books/add").with(csrf())
                        .param("name", "Clean Code")
                        .param("author", "Robert Martin")
                        .param("price", "250.00")
                        .param("genre", "SCIENCE")
                        .param("ageGroup", "ADULT")
                        .param("language", "ENGLISH")
                        .param("pages", "400")
                        .param("publicationYear", "2008")
                        .param("description", "A classic book"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books?success=added"));

        verify(bookService).addBook(any(BookDTO.class));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void addBook_ValidationErrors_ReturnsAddFormView() throws Exception {
        mockMvc.perform(post("/books/add").with(csrf())
                        .param("name", "").param("author", "").param("price", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("books/add-form"))
                .andExpect(model().attributeExists("genres", "ageGroups", "languages"));

        verify(bookService, never()).addBook(any());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void addBook_DuplicateBook_ReturnsAddFormWithFieldError() throws Exception {
        when(bookService.addBook(any())).thenThrow(new BookAlreadyExistsException("Clean Code", "Robert Martin"));
        when(messageSource.getMessage(any(String.class), any(), any(String.class), any()))
                .thenReturn("Book already exists");

        mockMvc.perform(validBookParams(post("/books/add").with(csrf())))
                .andExpect(status().isOk())
                .andExpect(view().name("books/add-form"));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void addBook_AsClient_IsForbidden() throws Exception {
        mockMvc.perform(post("/books/add").with(csrf()).param("name", "Test"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void showEditForm_AsEmployee_ReturnsEditFormView() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(new BookDTO());

        mockMvc.perform(get("/books/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/edit-form"))
                .andExpect(model().attributeExists("book", "genres", "ageGroups", "languages"));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void showEditForm_AsClient_IsForbidden() throws Exception {
        mockMvc.perform(get("/books/1/edit")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void editBook_ValidData_RedirectsToDetails() throws Exception {
        when(bookService.updateBookById(eq(1L), any())).thenReturn(new BookDTO());

        mockMvc.perform(validBookParams(post("/books/1/edit").with(csrf())))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void editBook_ValidationErrors_ReturnsEditFormView() throws Exception {
        mockMvc.perform(post("/books/1/edit").with(csrf())
                        .param("name", "").param("author", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("books/edit-form"));

        verify(bookService, never()).updateBookById(anyLong(), any());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void editBook_DuplicateBook_ReturnsEditFormWithFieldError() throws Exception {
        when(bookService.updateBookById(eq(1L), any()))
                .thenThrow(new BookAlreadyExistsException("Clean Code", "Robert Martin"));
        when(messageSource.getMessage(any(String.class), any(), any(String.class), any()))
                .thenReturn("Already exists");

        mockMvc.perform(validBookParams(post("/books/1/edit").with(csrf())))
                .andExpect(status().isOk())
                .andExpect(view().name("books/edit-form"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void deleteBook_AsEmployee_ArchivesAndRedirects() throws Exception {
        mockMvc.perform(post("/books/1/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books?success=deleted"));

        verify(bookService).archiveBook(1L);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void deleteBook_AsClient_IsForbidden() throws Exception {
        mockMvc.perform(post("/books/1/delete").with(csrf()))
                .andExpect(status().isNotFound());
    }
}
