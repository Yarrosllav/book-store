package com.epam.rd.autocode.spring.project.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "CLIENTS")
public class Client extends User{

    private BigDecimal balance;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean isBlocked = false;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL)
    private Basket basket;
}
