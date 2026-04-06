package com.epam.rd.autocode.spring.project.model;

import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Genre;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "BOOKS")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @Enumerated(EnumType.STRING)
    private Genre genre;
    @Enumerated(EnumType.STRING)
    @Column(name = "age_group")
    private AgeGroup ageGroup;
    private BigDecimal price;
    private LocalDate publicationDate;
    private String author;
    private Integer pages;
    private String characteristics;
    private String description;
    @Enumerated(EnumType.STRING)
    private Language language;
}
