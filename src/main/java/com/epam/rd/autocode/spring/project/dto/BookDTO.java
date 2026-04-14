package com.epam.rd.autocode.spring.project.dto;

import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Genre;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import com.epam.rd.autocode.spring.project.validation.PastOrPresentYear;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookDTO {
    private Long id;
    @NotBlank(message = "{error.validation.notblank}")
    @Size(max = 50, message = "{error.validation.bookname}")
    private String name;
    @NotNull(message = "{error.validation.notblank}")
    private Genre genre;
    @NotNull(message = "{error.validation.notblank}")
    private AgeGroup ageGroup;
    @NotNull(message = "{error.validation.notblank}")
    @NotNull(message = "{error.validation.notblank}")
    @Positive(message = "{error.validation.positive}")
    private BigDecimal price;
    @NotNull(message = "{error.validation.notblank}")
    @Min(value = 1800, message = "{error.validation.minyear}")
    @PastOrPresentYear
    private Integer publicationYear;
    @NotBlank(message = "{error.validation.notblank}")
    private String author;
    @NotNull(message = "{error.validation.notblank}")
    @Positive(message = "{error.validation.positive}")
    private Integer pages;
    @NotBlank(message = "{error.validation.notblank}")
    private String description;
    @NotNull(message = "{error.validation.notblank}")
    private Language language;
}
