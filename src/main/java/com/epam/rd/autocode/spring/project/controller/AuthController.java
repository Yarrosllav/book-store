package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.RegisterClientDTO;
import com.epam.rd.autocode.spring.project.exception.ClientAlreadyExistsException;
import com.epam.rd.autocode.spring.project.service.ClientService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
    private final MessageSource messageSource;

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
            @Valid @ModelAttribute("client") RegisterClientDTO clientDTO,
            BindingResult bindingResult,
            HttpServletRequest request){

        if(bindingResult.hasErrors()){
            return "register";
        }

        try{
            clientService.addClient(clientDTO);
            request.login(clientDTO.getEmail(), clientDTO.getPassword());
        }catch (ClientAlreadyExistsException e){

            String translatedMessage = messageSource.getMessage(
                    e.getMessage(),
                    null,
                    e.getMessage(),
                    LocaleContextHolder.getLocale()
            );

            bindingResult.rejectValue("email", "error.client", translatedMessage);
            return "register";
        }catch(ServletException e){
            return "redirect:/login?error=true";
        }

        return "redirect:/books?success=registered";

    }

}
