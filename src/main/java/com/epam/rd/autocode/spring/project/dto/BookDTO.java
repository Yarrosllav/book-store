package com.epam.rd.autocode.spring.project.dto;

import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Genre;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookDTO {
    private Long id;
    private String name;
    private Genre genre;
    private AgeGroup ageGroup;
    private BigDecimal price;
    private LocalDate publicationDate;
    private String author;
    private Integer pages;
    private String characteristics;
    private String description;
    private Language language;
}
