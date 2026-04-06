package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.dto.RegisterClientDTO;
import com.epam.rd.autocode.spring.project.dto.UpdateClientDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Basket;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.enums.Role;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.OrderRepository;
import com.epam.rd.autocode.spring.project.repo.UserRepository;
import com.epam.rd.autocode.spring.project.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper mapper;


    @Override
    @Transactional(readOnly = true)
    public List<ClientDTO> getAllClients() {
        log.info("Getting all clients");
        return clientRepository.findAll().stream().map(c -> mapper.map(c, ClientDTO.class)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDTO getClientByEmail(String email) {
        log.info("Getting client by email: {}", email);
        return clientRepository.findByEmail(email)
                .map(c -> mapper.map(c, ClientDTO.class))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDTO getClientById(Long id) {
        log.info("Getting client by id: {}", id);
        return mapper.map(clientRepository.findById(id).orElseThrow(NotFoundException::new), ClientDTO.class);
    }

    @Override
    @Transactional
    public ClientDTO updateClient(Long id, UpdateClientDTO clientDTO) {
        log.info("Updating client by id: {}", id);

        Client client = clientRepository.findById(id).orElseThrow(NotFoundException::new);

        if(clientDTO.getEmail() != null && !client.getEmail().equals(clientDTO.getEmail()) && userRepository.existsByEmail(clientDTO.getEmail())) {
            throw new AlreadyExistException("Email " + clientDTO.getEmail() + " already exists");
        }

        if (clientDTO.getName() != null) {
            client.setName(clientDTO.getName());
        }
        if (clientDTO.getEmail() != null) {
            client.setEmail(clientDTO.getEmail());
        }
        if (clientDTO.getBalance() != null && clientDTO.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            client.setBalance(client.getBalance().add(clientDTO.getBalance()));
        }

        if(clientDTO.getPassword() != null && !clientDTO.getPassword().isBlank()) {
            client.setPassword(passwordEncoder.encode(clientDTO.getPassword()));
        }
        clientRepository.save(client);

        return mapper.map(client, ClientDTO.class);
    }

    @Override
    @Transactional
    public void deleteClient(Long id) {
        log.info("Deleting client by id: {}", id);

        Client client = clientRepository.findById(id).orElseThrow(NotFoundException::new);
        orderRepository.deleteAllByClientId(id);

        clientRepository.delete(client);
    }

    @Override
    @Transactional
    public ClientDTO addClient(RegisterClientDTO clientDTO) {
        log.info("Adding client with email: {}", clientDTO.getEmail());

        if (userRepository.existsByEmail(clientDTO.getEmail())) {
            throw new AlreadyExistException("User with email " + clientDTO.getEmail() + " already exists");
        }

        Client client = mapper.map(clientDTO, Client.class);

        String encodedPassword = passwordEncoder.encode(clientDTO.getPassword());
        client.setPassword(encodedPassword);
        client.setRole(Role.CLIENT);

        Basket basket = new Basket();
        basket.setClient(client);
        client.setBasket(basket);

        if(client.getBalance() == null) {
            client.setBalance(BigDecimal.ZERO);
        }

        Client savedClient = clientRepository.save(client);

        return mapper.map(savedClient, ClientDTO.class);
    }

    @Override
    @Transactional
    public void blockClient(Long id) {
        Client client = clientRepository.findById(id).orElseThrow(NotFoundException::new);
        client.setIsBlocked(true);
        clientRepository.save(client);
    }

    @Override
    @Transactional
    public void unblockClient(Long id) {
        Client client = clientRepository.findById(id).orElseThrow(NotFoundException::new);
        client.setIsBlocked(false);
        clientRepository.save(client);
    }
}
