package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.dto.RegisterClientDTO;
import com.epam.rd.autocode.spring.project.dto.UpdateClientDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ClientService {

    List<ClientDTO> getAllClients();

    ClientDTO getClientByEmail(String email);

    ClientDTO getClientById(Long id);

    ClientDTO updateClient(Long id, UpdateClientDTO client);

    void deleteClient(Long id);

    ClientDTO addClient(RegisterClientDTO client);

    void blockClient(Long id);

    void unblockClient(Long id);

    void topUpBalance(String email, BigDecimal amount);

    Page<ClientDTO> searchClients(String search, Pageable pageable);

}
