package com.epam.rd.autocode.spring.project.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateClientDTO {
    @Email
    private String email;

    @Pattern(regexp = "^$|.{6,}", message = "{error.validation.password_size}")
    private String password;

    @NotBlank(message = "{error.validation.notblank}")
    @Size(max = 50, message = "{error.validation.name}")
    private String name;

    private Boolean isBlocked;
}
