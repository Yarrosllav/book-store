package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/clients")
@RequiredArgsConstructor
@PreAuthorize("hasRole('EMPLOYEE')")
public class AdminController {
    private final ClientService clientService;

    @GetMapping
    public String getAllClients(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            Model model){

        Pageable pageable = PageRequest.of(page, 15, Sort.by("id").descending());
        Page<ClientDTO> clientsPage = clientService.searchClients(search, pageable);

        model.addAttribute("clients", clientsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", clientsPage.getTotalPages());
        model.addAttribute("search", search);
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
}
