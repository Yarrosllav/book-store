package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.dto.RegisterClientDTO;
import com.epam.rd.autocode.spring.project.dto.UpdateClientDTO;

import java.util.List;

public interface ClientService {

    List<ClientDTO> getAllClients();

    ClientDTO getClientByEmail(String email);

    ClientDTO getClientById(Long id);

    ClientDTO updateClient(Long id, UpdateClientDTO client);

    void deleteClient(Long id);

    ClientDTO addClient(RegisterClientDTO client);
}
