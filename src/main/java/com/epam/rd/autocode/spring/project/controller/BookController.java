package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.dto.BookFilterDTO;
import com.epam.rd.autocode.spring.project.dto.CreateBookDTO;
import com.epam.rd.autocode.spring.project.dto.UpdateBookDTO;
import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Genre;
import com.epam.rd.autocode.spring.project.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping
    public String getAllBooks(Model model,
                              @ModelAttribute BookFilterDTO filter,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "30") int size,
                              @RequestParam(defaultValue = "name") String sortBy,
                              @RequestParam(defaultValue = "asc") String sortDir){

        Page<BookDTO> books = bookService.getAllBooks(filter, page, size, sortBy, sortDir);

        model.addAttribute("books", books.getContent());
        model.addAttribute("filter", filter);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", books.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("genres", Genre.values());
        model.addAttribute("ageGroups", AgeGroup.values());


        return "books/catalog";
    }

    @GetMapping("/{id}")
    public String getBookDetails(@PathVariable Long id, Model model){
        model.addAttribute("book", bookService.getBookById(id));
        return "books/details";
    }


    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/add")
    public String showAddBookForm(Model model){
        model.addAttribute("book", new CreateBookDTO());
        return "books/add-form";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/add")
    public String addBook(@Valid @ModelAttribute("book") CreateBookDTO bookDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "books/add-form";
        }
        bookService.addBook(bookDTO);
        return "redirect:/books?success=added";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model){
        model.addAttribute("book", bookService.getBookById(id));
        return "books/edit-form";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/{id}/edit")
    public String editBook(@PathVariable Long id, @Valid @ModelAttribute("book") UpdateBookDTO bookDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "books/edit-form";
        }
        bookService.updateBookById(id, bookDTO);
        return "redirect:/books/" + id + "?success=updated";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/{id}/delete")
    public String deleteBook(@PathVariable Long id){
        bookService.deleteBook(id);
        return "redirect:/books?success=deleted";
    }



}
