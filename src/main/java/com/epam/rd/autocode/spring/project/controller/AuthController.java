package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.RegisterClientDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final ClientService clientService;

    @GetMapping("/login")
    public String showLoginPage(){
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model){
        model.addAttribute("client", new RegisterClientDTO());
        return "register";
    }

    @PostMapping("/register")
    public String registerClient(
            @Valid @ModelAttribute RegisterClientDTO clientDTO,
            BindingResult bindingResult,
            Model model){

        if(bindingResult.hasErrors()){
            return "register";
        }

        try{
            clientService.addClient(clientDTO);
        }catch (AlreadyExistException e){
            bindingResult.rejectValue("email", "error.client", e.getMessage());
        }

        return "redirect:/login?success=true";

    }

}
