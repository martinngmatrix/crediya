package co.com.bancolombia.api.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginUserDTO(
    @NotBlank(message = "El correo no puede ser nulo ni vacío")
    String email,
    @NotBlank(message = "La contraseña no puede ser nulo ni vacío")
    String password
) {}
