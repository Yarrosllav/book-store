package com.epam.rd.autocode.spring.project.advice;

import com.epam.rd.autocode.spring.project.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.math.BigDecimal;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private final ClientService clientService;

    @ModelAttribute("currentBalance")
    public BigDecimal addCurrentBalance(Authentication authentication){
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CLIENT"))) {
                return clientService.getClientByEmail(authentication.getName()).getBalance();
            }
        }
        return null;
    }

}
