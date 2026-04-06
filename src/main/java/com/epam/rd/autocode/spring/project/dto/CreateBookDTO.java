package com.epam.rd.autocode.spring.project.dto;

import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Genre;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateBookDTO {
    @NotBlank
    private String name;

    @NotBlank
    private Genre genre;

    @NotNull
    private AgeGroup ageGroup;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal price;

    @NotNull
    private LocalDate publicationDate;

    @NotBlank
    private String author;

    @NotNull
    @Min(value = 1)
    private Integer pages;

    private String characteristics;
    private String description;

    @NotNull
    private Language language;
}
