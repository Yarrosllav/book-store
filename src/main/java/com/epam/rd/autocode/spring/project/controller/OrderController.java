package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.CreateOrderDTO;
import com.epam.rd.autocode.spring.project.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/checkout")
    public String checkout(@AuthenticationPrincipal UserDetails currentUser){
        CreateOrderDTO orderDTO = new CreateOrderDTO(currentUser.getUsername());
        orderService.addOrder(orderDTO);
        return "redirect:/orders/my?success=orderPlaced";
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/my")
    public String myOrders(@AuthenticationPrincipal UserDetails currentUser, Model model){
        model.addAttribute("orders", orderService.getOrdersByClient(currentUser.getUsername()));
        return "orders/client-orders";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/waiting")
    public String waitingOrders(Model model){
        model.addAttribute("orders", orderService.getWaitingOrders());
        return "orders/waiting-orders";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/processed")
    public String processedOrders(@AuthenticationPrincipal UserDetails currentEmployee, Model model){
        model.addAttribute("orders", orderService.getOrdersByEmployee(currentEmployee.getUsername()));
        return "orders/employee-orders";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/{id}/confirm")
    public String confirmOrder(@PathVariable Long id, @AuthenticationPrincipal UserDetails currentEmployee){
        orderService.confirmOrder(id, currentEmployee.getUsername());
        return "redirect:/orders/waiting?success=confirmed";
    }
}
