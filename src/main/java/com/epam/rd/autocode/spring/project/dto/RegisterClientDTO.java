package com.epam.rd.autocode.spring.project.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterClientDTO {
    @NotBlank(message = "{error.validation.notblank}")
    @Email(message = "{error.validation.email}")
    private String email;

    @NotBlank(message = "{error.validation.notblank}")
    @Size(min = 6, message = "{error.validation.password_size}")
    private String password;

    @NotBlank(message = "{error.validation.notblank}")
    private String name;

}
