package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.dto.UpdateClientDTO;
import com.epam.rd.autocode.spring.project.dto.UpdateEmployeeDTO;
import com.epam.rd.autocode.spring.project.service.ClientService;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ClientService clientService;
    private final EmployeeService employeeService;

    @GetMapping
    public String viewProfile(@AuthenticationPrincipal UserDetails currentUser, Model model){
        String email = currentUser.getUsername();

        boolean isClient = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"));

        if(isClient){
            ClientDTO client = clientService.getClientByEmail(email);
            model.addAttribute("user", client);
            model.addAttribute("role", "CLIENT");
        }else{
            EmployeeDTO employee = employeeService.getEmployeeByEmail(email);
            model.addAttribute("user", employee);
            model.addAttribute("role", "EMPLOYEE");
        }
        return "profile/view";
    }

    @GetMapping("/edit")
    public String showEditForm(@AuthenticationPrincipal UserDetails currentUser, Model model){

        String email = currentUser.getUsername();
        boolean isClient = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"));

        if(isClient){
            ClientDTO client = clientService.getClientByEmail(email);
            UpdateClientDTO updateDto = new UpdateClientDTO(client.getEmail(), null, client.getName(), false);
            model.addAttribute("clientDto", updateDto);
            return "profile/edit-client";
        }else{
            EmployeeDTO employee = employeeService.getEmployeeByEmail(email);
            UpdateEmployeeDTO updateDto = new UpdateEmployeeDTO(employee.getEmail(), null,
                    employee.getName(), employee.getBirthDate(), employee.getPhone());
            model.addAttribute("employeeDto", updateDto);
            return "profile/edit-employee";
        }
    }


    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/edit/client")
    public String editClientProfile(@AuthenticationPrincipal UserDetails currentUser,
                                    @Valid @ModelAttribute("clientDto") UpdateClientDTO clientDto,
                                    BindingResult result,
                                    Model model){

        if(result.hasErrors()) {
            clientDto.setEmail(currentUser.getUsername());
            model.addAttribute("clientDto", clientDto);
            return "profile/edit-client";
        }

        ClientDTO client = clientService.getClientByEmail(currentUser.getUsername());
        clientService.updateClient(client.getId(), clientDto);

        return "redirect:/profile?success=updated";
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/delete")
    public String deleteAccount(@AuthenticationPrincipal UserDetails currentUser, HttpServletRequest request) throws ServletException {
        ClientDTO client = clientService.getClientByEmail(currentUser.getUsername());
        clientService.deleteClient(client.getId());
        request.logout();

        return "redirect:/?success=accountDeleted";

    }

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/balance/topup")
    public String topUpBalance(@AuthenticationPrincipal UserDetails currentUser,
                               @RequestParam BigDecimal amount,HttpServletRequest request) throws ServletException {
        clientService.topUpBalance(currentUser.getUsername(), amount);

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/books");
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/edit/employee")
    public String editEmployeeProfile(@AuthenticationPrincipal UserDetails currentUser,
                                     @Valid @ModelAttribute("employeeDto") UpdateEmployeeDTO employeeDto,
                                     BindingResult result,
                                      Model model){

        if(result.hasErrors()) {
            return "profile/edit-employee";
        }

        EmployeeDTO employee = employeeService.getEmployeeByEmail(currentUser.getUsername());
        employeeService.updateEmployee(employee.getId(), employeeDto);

        return "redirect:/profile?success=updated";

    }


}
