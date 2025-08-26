package co.com.bancolombia.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserDTO(
        @NotBlank(message = "El nombre no puede ser nulo ni vacío")
        String name,

        @NotBlank(message = "El apellido no puede ser nulo ni vacío")
        String lastName,

        LocalDate dateOfBirth,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Correo inválido")
        String email,

        String address,
        String phone,

        @NotNull(message = "El salario base es obligatorio")
        @DecimalMin(value = "0.0", inclusive = true, message = "El salario base no puede ser menor que 0")
        @DecimalMax(value = "15000000.0", inclusive = true, message = "El salario base no puede ser mayor que 15,000,000")
        BigDecimal baseSalary
) {}
