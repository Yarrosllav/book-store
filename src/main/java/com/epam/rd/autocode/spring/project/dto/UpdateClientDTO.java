package com.epam.rd.autocode.spring.project.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateClientDTO {
    @Email
    private String email;

    @Pattern(regexp = "^$|.{6,}", message = "Пароль має бути не менше 6 символів")
    private String password;

    private String name;
    private Boolean isBlocked;

    @DecimalMin(value = "0")
    private BigDecimal balance;
}
