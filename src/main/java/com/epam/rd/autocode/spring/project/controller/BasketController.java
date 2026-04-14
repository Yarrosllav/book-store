package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BasketDTO;
import com.epam.rd.autocode.spring.project.service.BasketService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/basket")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CLIENT')")
public class BasketController {

    private final BasketService basketService;

    @GetMapping
    public String viewBasket(@AuthenticationPrincipal UserDetails currentUser, Model model){
        BasketDTO basketDTO = basketService.getBasketByClientEmail(currentUser.getUsername());
        model.addAttribute("basket", basketDTO);
        return "basket/view";
    }

    @PostMapping("/add/{bookid}")
    public String addBook(@PathVariable Long bookid,
                          @RequestParam(defaultValue = "1") Integer quantity,
                          @AuthenticationPrincipal UserDetails currentUser){

        basketService.addBookToBasket(currentUser.getUsername(), bookid, quantity);
        return "redirect:/books?success=addedToBasket";
    }

    @PostMapping("/remove/{itemid}")
    public String removeBook(@PathVariable Long itemid,
                             @AuthenticationPrincipal UserDetails currentUser){

        basketService.removeBookFromBasket(currentUser.getUsername(), itemid);
        return "redirect:/basket?success=removed";
    }

    @PostMapping("/update/{itemid}")
    public String updateQuantity(
            @PathVariable Long itemid,
            @RequestParam Integer delta,
            @AuthenticationPrincipal UserDetails currentUser
    ){
        basketService.updateQuantity(currentUser.getUsername(), itemid, delta);
        return "redirect:/basket";
    }
}
