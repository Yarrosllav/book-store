package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.CreateOrderDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public String getOrderDetails(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails currentUser) {
        OrderDTO order = orderService.getOrderById(id);

        boolean isEmployee = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"));
        if(!isEmployee && !order.getClientEmail().equals(currentUser.getUsername())){
            throw new AccessDeniedException("error.exception.access_denied");
        }
        model.addAttribute("order", order);
        return "orders/details";
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/checkout")
    public String checkout(@AuthenticationPrincipal UserDetails currentUser) {
        CreateOrderDTO orderDTO = new CreateOrderDTO(currentUser.getUsername());
        orderService.addOrder(orderDTO);
        return "redirect:/orders/my?success=orderPlaced";
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/my")
    public String myOrders(
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<OrderDTO> ordersPage = orderService.getOrdersByClient(currentUser.getUsername(), pageable);

        model.addAttribute("orders", ordersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ordersPage.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);

        return "orders/client-orders";
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/{orderId}/cancel")
    public String cancelOrder(@PathVariable Long orderId, @AuthenticationPrincipal UserDetails currentUser) {
        orderService.cancelOrderByClient(orderId, currentUser.getUsername());
        return "redirect:/orders/my?success=cancelled";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/waiting")
    public String waitingOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        Sort sort = sortDir.equals("asc") ? Sort.by("orderDate").ascending() : Sort.by("orderDate").descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<OrderDTO> ordersPage = orderService.getWaitingOrders(search, pageable);

        model.addAttribute("orders", ordersPage.getContent());
        model.addAttribute("search", search);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ordersPage.getTotalPages());
        model.addAttribute("sortDir", sortDir);

        return "orders/waiting-orders";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/processed")
    public String processedOrdersHistory(
            @AuthenticationPrincipal UserDetails currentEmployee,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "false") boolean onlyMy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Sort sort = sortDir.equals("asc") ? Sort.by("orderDate").ascending() : Sort.by("orderDate").descending();
        Pageable pageable = PageRequest.of(page, 15, sort);

        Page<OrderDTO> ordersPage = orderService.getOrderHistoryWithSearch(search, onlyMy, currentEmployee.getUsername(), pageable);

        model.addAttribute("orders", ordersPage.getContent());
        model.addAttribute("search", search);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ordersPage.getTotalPages());
        model.addAttribute("onlyMy", onlyMy);
        model.addAttribute("sortDir", sortDir);

        return "orders/employee-orders";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/{id}/confirm")
    public String confirmOrder(@PathVariable Long id, @AuthenticationPrincipal UserDetails currentEmployee) {
        orderService.confirmOrder(id, currentEmployee.getUsername());
        return "redirect:/orders/waiting?success=confirmed";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/{id}/refund")
    public String refundOrder(@PathVariable Long id, @AuthenticationPrincipal UserDetails currentEmployee) {
        orderService.refundOrder(id, currentEmployee.getUsername());
        return "redirect:/orders/processed?success=refunded";
    }
}
