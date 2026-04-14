package com.epam.rd.autocode.spring.project.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateEmployeeDTO {
    @Email
    private String email;

    @Pattern(regexp = "^$|.{6,}", message = "{error.validation.password_size}")
    private String password;

    @NotBlank(message = "{error.validation.notblank}")
    @Size(max = 50, message = "{error.validation.name}")
    private String name;

    @NotNull(message = "{error.validation.notblank}")
    @Past(message = "{error.validation.past_date}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @NotBlank(message = "{error.validation.notblank}")
    @Pattern(regexp = "^\\+?[0-9\\s\\-()]{10,20}$", message = "{error.validation.phone}")
    private String phone;
}
