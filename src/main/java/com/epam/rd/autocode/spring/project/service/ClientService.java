package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;

import java.util.List;

public interface ClientService {

    List<ClientDTO> getAllClients();

    ClientDTO getClientByEmail(String email);

    ClientDTO getClientById(Long id);

    ClientDTO updateClient(Long id, ClientDTO client);

    void deleteClient(Long id);

    ClientDTO addClient(ClientDTO client);
}
