package com.epam.rd.autocode.spring.project.dto;

import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
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
public class UpdateBookDTO {
    private String name;
    private String genre;
    private AgeGroup ageGroup;
    @DecimalMin(value = "0")
    private BigDecimal price;
    private LocalDate publicationDate;
    private String author;
    @Min(value = 1)
    private Integer pages;
    private String characteristics;
    private String description;
    private Language language;
}
