package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.conf.SecurityConfig;
import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.service.ClientService;
import com.epam.rd.autocode.spring.project.service.impl.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

@Import({SecurityConfig.class})
public abstract class BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected UserDetailsServiceImpl userDetailsService;

    @MockBean
    protected ClientService clientService;

    @MockBean
    protected MessageSource messageSource;

    @BeforeEach
    void setUpGlobalMocks() {
        ClientDTO fakeClient = new ClientDTO();
        fakeClient.setBalance(BigDecimal.ZERO);

        lenient().when(clientService.getClientByEmail(anyString())).thenReturn(fakeClient);

        lenient().when(messageSource.getMessage(anyString(), any(), anyString(), any()))
                .thenReturn("Mocked Translated Message");
    }
}

