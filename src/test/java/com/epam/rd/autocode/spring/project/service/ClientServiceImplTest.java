package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.dto.RegisterClientDTO;
import com.epam.rd.autocode.spring.project.dto.UpdateClientDTO;
import com.epam.rd.autocode.spring.project.exception.ClientAlreadyExistsException;
import com.epam.rd.autocode.spring.project.exception.ClientNotFoundException;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.enums.Role;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.OrderRepository;
import com.epam.rd.autocode.spring.project.repo.UserRepository;
import com.epam.rd.autocode.spring.project.service.impl.ClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock private ClientRepository clientRepository;
    @Mock private UserRepository userRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private ModelMapper mapper;

    @InjectMocks
    private ClientServiceImpl clientService;

    private Client client;
    private ClientDTO clientDTO;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId(1L);
        client.setEmail("user@example.com");
        client.setName("John");
        client.setPassword("encoded_pass");
        client.setBalance(new BigDecimal("100.00"));
        client.setRole(Role.CLIENT);

        clientDTO = new ClientDTO();
        clientDTO.setId(1L);
        clientDTO.setEmail("user@example.com");
        clientDTO.setName("John");
    }

    @Test
    void getAllClients_ReturnsListOfDTOs() {
        when(clientRepository.findAll()).thenReturn(List.of(client));
        when(mapper.map(client, ClientDTO.class)).thenReturn(clientDTO);

        List<ClientDTO> result = clientService.getAllClients();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("user@example.com");
    }

    @Test
    void getAllClients_EmptyList_ReturnsEmptyList() {
        when(clientRepository.findAll()).thenReturn(List.of());

        List<ClientDTO> result = clientService.getAllClients();

        assertThat(result).isEmpty();
    }

    @Test
    void getClientByEmail_ExistingEmail_ReturnsDTO() {
        when(clientRepository.findByEmail("user@example.com")).thenReturn(Optional.of(client));
        when(mapper.map(client, ClientDTO.class)).thenReturn(clientDTO);

        ClientDTO result = clientService.getClientByEmail("user@example.com");

        assertThat(result.getEmail()).isEqualTo("user@example.com");
    }

    @Test
    void getClientByEmail_NonExistingEmail_ThrowsClientNotFoundException() {
        when(clientRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.getClientByEmail("nobody@example.com"))
                .isInstanceOf(ClientNotFoundException.class);
    }

    @Test
    void getClientById_ExistingId_ReturnsDTO() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(mapper.map(client, ClientDTO.class)).thenReturn(clientDTO);

        ClientDTO result = clientService.getClientById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getClientById_NonExistingId_ThrowsClientNotFoundException() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.getClientById(99L))
                .isInstanceOf(ClientNotFoundException.class);
    }

    @Test
    void updateClient_WithNewPassword_EncodesAndSaves() {
        UpdateClientDTO dto = new UpdateClientDTO();
        dto.setName("UpdatedName");
        dto.setEmail("new@example.com");
        dto.setPassword("newPass123");

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(passwordEncoder.encode("newPass123")).thenReturn("encoded_new_pass");
        when(clientRepository.save(client)).thenReturn(client);
        when(mapper.map(client, ClientDTO.class)).thenReturn(clientDTO);

        clientService.updateClient(1L, dto);

        assertThat(client.getName()).isEqualTo("UpdatedName");
        assertThat(client.getEmail()).isEqualTo("new@example.com");
        assertThat(client.getPassword()).isEqualTo("encoded_new_pass");
        verify(clientRepository).save(client);
    }

    @Test
    void updateClient_WithBlankPassword_DoesNotEncodeOrChangePassword() {
        UpdateClientDTO dto = new UpdateClientDTO();
        dto.setName("UpdatedName");
        dto.setEmail("new@example.com");
        dto.setPassword("   ");

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientRepository.save(client)).thenReturn(client);
        when(mapper.map(client, ClientDTO.class)).thenReturn(clientDTO);

        clientService.updateClient(1L, dto);

        assertThat(client.getPassword()).isEqualTo("encoded_pass");
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void updateClient_WithNullPassword_DoesNotEncodeOrChangePassword() {
        UpdateClientDTO dto = new UpdateClientDTO();
        dto.setName("UpdatedName");
        dto.setEmail("new@example.com");
        dto.setPassword(null);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientRepository.save(client)).thenReturn(client);
        when(mapper.map(client, ClientDTO.class)).thenReturn(clientDTO);

        clientService.updateClient(1L, dto);

        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void updateClient_ClientNotFound_ThrowsClientNotFoundException() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.updateClient(99L, new UpdateClientDTO()))
                .isInstanceOf(ClientNotFoundException.class);

        verify(clientRepository, never()).save(any());
    }

    @Test
    void deleteClient_ExistingClient_DeletesOrdersAndClient() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        clientService.deleteClient(1L);

        verify(orderRepository).deleteAllByClientId(1L);
        verify(clientRepository).delete(client);
    }

    @Test
    void deleteClient_NonExistingClient_ThrowsClientNotFoundException() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.deleteClient(99L))
                .isInstanceOf(ClientNotFoundException.class);

        verify(orderRepository, never()).deleteAllByClientId(any());
        verify(clientRepository, never()).delete(any());
    }

    @Test
    void addClient_NewClient_RegistersSuccessfully() {
        RegisterClientDTO dto = new RegisterClientDTO();
        dto.setEmail("new@example.com");
        dto.setPassword("password123");
        dto.setName("Alice");

        Client newClient = new Client();
        newClient.setEmail("new@example.com");

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(mapper.map(dto, Client.class)).thenReturn(newClient);
        when(passwordEncoder.encode("password123")).thenReturn("hashed_pass");
        when(clientRepository.save(newClient)).thenReturn(newClient);
        when(mapper.map(newClient, ClientDTO.class)).thenReturn(clientDTO);

        ClientDTO result = clientService.addClient(dto);

        assertThat(newClient.getPassword()).isEqualTo("hashed_pass");
        assertThat(newClient.getRole()).isEqualTo(Role.CLIENT);
        assertThat(newClient.getBasket()).isNotNull();
        assertThat(newClient.getBasket().getClient()).isEqualTo(newClient);
        verify(clientRepository).save(newClient);
    }

    @Test
    void addClient_SetsDefaultZeroBalance_WhenBalanceIsNull() {
        RegisterClientDTO dto = new RegisterClientDTO();
        dto.setEmail("new@example.com");
        dto.setPassword("pass");

        Client newClient = new Client();
        newClient.setBalance(null);

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(mapper.map(dto, Client.class)).thenReturn(newClient);
        when(passwordEncoder.encode(any())).thenReturn("hash");
        when(clientRepository.save(newClient)).thenReturn(newClient);
        when(mapper.map(newClient, ClientDTO.class)).thenReturn(clientDTO);

        clientService.addClient(dto);

        assertThat(newClient.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void addClient_DoesNotOverrideExistingBalance() {
        RegisterClientDTO dto = new RegisterClientDTO();
        dto.setEmail("new@example.com");
        dto.setPassword("pass");

        Client newClient = new Client();
        newClient.setBalance(new BigDecimal("50.00"));

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(mapper.map(dto, Client.class)).thenReturn(newClient);
        when(passwordEncoder.encode(any())).thenReturn("hash");
        when(clientRepository.save(newClient)).thenReturn(newClient);
        when(mapper.map(newClient, ClientDTO.class)).thenReturn(clientDTO);

        clientService.addClient(dto);

        assertThat(newClient.getBalance()).isEqualByComparingTo("50.00");
    }

    @Test
    void addClient_DuplicateEmail_ThrowsClientAlreadyExistsException() {
        RegisterClientDTO dto = new RegisterClientDTO();
        dto.setEmail("user@example.com");

        when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

        assertThatThrownBy(() -> clientService.addClient(dto))
                .isInstanceOf(ClientAlreadyExistsException.class);

        verify(clientRepository, never()).save(any());
    }

    @Test
    void blockClient_ExistingClient_SetsIsBlockedTrue() {
        client.setIsBlocked(false);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        clientService.blockClient(1L);

        assertThat(client.getIsBlocked()).isTrue();
        verify(clientRepository).save(client);
    }

    @Test
    void blockClient_ClientNotFound_ThrowsClientNotFoundException() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.blockClient(99L))
                .isInstanceOf(ClientNotFoundException.class);
    }

    @Test
    void unblockClient_ExistingClient_SetsIsBlockedFalse() {
        client.setIsBlocked(true);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        clientService.unblockClient(1L);

        assertThat(client.getIsBlocked()).isFalse();
        verify(clientRepository).save(client);
    }

    @Test
    void unblockClient_ClientNotFound_ThrowsClientNotFoundException() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.unblockClient(99L))
                .isInstanceOf(ClientNotFoundException.class);
    }

    @Test
    void topUpBalance_ExistingClient_AddsAmountToBalance() {
        when(clientRepository.findByEmail("user@example.com")).thenReturn(Optional.of(client));

        clientService.topUpBalance("user@example.com", new BigDecimal("50.00"));

        assertThat(client.getBalance()).isEqualByComparingTo("150.00");
        verify(clientRepository).save(client);
    }

    @Test
    void topUpBalance_ClientNotFound_ThrowsClientNotFoundException() {
        when(clientRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.topUpBalance("nobody@example.com", BigDecimal.TEN))
                .isInstanceOf(ClientNotFoundException.class);
    }

    @Test
    void searchClients_WithSearch_PassesSearchToRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Client> clientPage = new PageImpl<>(List.of(client));

        when(clientRepository.searchClients("john", pageable)).thenReturn(clientPage);
        when(mapper.map(client, ClientDTO.class)).thenReturn(clientDTO);

        Page<ClientDTO> result = clientService.searchClients("john", pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(clientRepository).searchClients("john", pageable);
    }

    @Test
    void searchClients_WithBlankSearch_PassesNullToRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Client> emptyPage = new PageImpl<>(List.of());

        when(clientRepository.searchClients(null, pageable)).thenReturn(emptyPage);

        clientService.searchClients("  ", pageable);

        verify(clientRepository).searchClients(null, pageable);
    }

    @Test
    void searchClients_WithNullSearch_PassesNullToRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Client> emptyPage = new PageImpl<>(List.of());

        when(clientRepository.searchClients(null, pageable)).thenReturn(emptyPage);

        clientService.searchClients(null, pageable);

        verify(clientRepository).searchClients(null, pageable);
    }
}
