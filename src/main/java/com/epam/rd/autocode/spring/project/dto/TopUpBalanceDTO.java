package com.epam.rd.autocode.spring.project.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TopUpBalanceDTO {
    @NotNull(message = "{error.validation.notblank}")
    @Positive(message = "{error.validation.positive}")
    private BigDecimal amount;
}
