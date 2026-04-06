package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.service.ClientService;
import com.epam.rd.autocode.spring.project.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/clients")
@RequiredArgsConstructor
@PreAuthorize("hasRole('EMPLOYEE')")
public class AdminController {
    private final ClientService clientService;
    private final OrderService orderService;

    @GetMapping
    public String getAllClients(Model model){
        model.addAttribute("clients", clientService.getAllClients());
        return "admin/clients-list";
    }

    @PostMapping("/{id}/block")
    public String blockClient(@PathVariable Long id){
        clientService.blockClient(id);
        return "redirect:/admin/clients?success=blocked";
    }

    @PostMapping("/{id}/unblock")
    public String unblockClient(@PathVariable Long id){
        clientService.unblockClient(id);
        return "redirect:/admin/clients?success=unblocked";
    }

    @GetMapping("/history")
    public String myOrders(@AuthenticationPrincipal UserDetails currentUser, Model model){
        model.addAttribute("orders", orderService.getOrdersByEmployee(currentUser.getUsername()));
        return "admin/orders-history";
    }




}
