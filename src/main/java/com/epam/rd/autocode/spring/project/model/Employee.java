package com.epam.rd.autocode.spring.project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Entity
@Table(name = "EMPLOYEES")
public class Employee extends User{
    @Column(name = "BIRTH_DATE")
    private LocalDate birthDate;
    @Column(name = "PHONE")
    private String phone;

    public Employee(Long id, String email, String password, String name, LocalDate birthDate, String phone) {
        super(id, email, password, name);
        this.birthDate = birthDate;
        this.phone = phone;
    }
}
