package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.dto.RegisterClientDTO;
import com.epam.rd.autocode.spring.project.dto.UpdateClientDTO;
import com.epam.rd.autocode.spring.project.exception.ClientAlreadyExistsException;
import com.epam.rd.autocode.spring.project.exception.ClientNotFoundException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        return clientRepository.findAll().stream().map(c -> mapper.map(c, ClientDTO.class)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDTO getClientByEmail(String email) {
        return clientRepository.findByEmail(email)
                .map(c -> mapper.map(c, ClientDTO.class))
                .orElseThrow(ClientNotFoundException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDTO getClientById(Long id) {
        return mapper.map(clientRepository.findById(id).orElseThrow(ClientNotFoundException::new), ClientDTO.class);
    }

    @Override
    @Transactional
    public ClientDTO updateClient(Long id, UpdateClientDTO clientDTO) {
        Client client = clientRepository.findById(id).orElseThrow(ClientNotFoundException::new);

        client.setName(clientDTO.getName());
        client.setEmail(clientDTO.getEmail());

        if(clientDTO.getPassword() != null && !clientDTO.getPassword().isBlank()) {
            client.setPassword(passwordEncoder.encode(clientDTO.getPassword()));
        }

        clientRepository.save(client);

        log.info("Profile of client ID={} successfully updated", id);
        return mapper.map(client, ClientDTO.class);
    }

    @Override
    @Transactional
    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id).orElseThrow(ClientNotFoundException::new);
        orderRepository.deleteAllByClientId(id);

        log.warn("Client ID={} and all their associated data have been permanently deleted", id);
        clientRepository.delete(client);
    }

    @Override
    @Transactional
    public ClientDTO addClient(RegisterClientDTO clientDTO) {
        if (userRepository.existsByEmail(clientDTO.getEmail())) {
            throw new ClientAlreadyExistsException();
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

        log.info("New client successfully registered: {}", savedClient.getEmail());
        return mapper.map(savedClient, ClientDTO.class);
    }

    @Override
    @Transactional
    public void blockClient(Long id) {
        Client client = clientRepository.findById(id).orElseThrow(ClientNotFoundException::new);
        client.setIsBlocked(true);
        clientRepository.save(client);
        log.warn("ADMIN ACTION: Client ID={} has been BLOCKED", id);
    }

    @Override
    @Transactional
    public void unblockClient(Long id) {
        Client client = clientRepository.findById(id).orElseThrow(ClientNotFoundException::new);
        client.setIsBlocked(false);
        clientRepository.save(client);
        log.info("ADMIN ACTION: Client ID={} has been UNBLOCKED", id);
    }

    @Override
    @Transactional
    public void topUpBalance(String email, BigDecimal amount) {
        Client client = clientRepository.findByEmail(email).orElseThrow(ClientNotFoundException::new);
        client.setBalance(client.getBalance().add(amount));
        clientRepository.save(client);
        log.info("Client [{}] topped up balance by {} UAH", email, amount);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientDTO> searchClients(String search, Pageable pageable) {
        String finalSearch = (search != null && !search.isBlank()) ? search : null;
        return clientRepository.searchClients(finalSearch, pageable).map(c -> mapper.map(c, ClientDTO.class));
    }
}
