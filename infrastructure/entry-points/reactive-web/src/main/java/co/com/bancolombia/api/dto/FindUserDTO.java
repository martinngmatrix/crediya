package co.com.bancolombia.api.dto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

public record FindUserDTO (
        BigInteger id,
        String name,
        String lastName,
        LocalDate dateOfBirth,
        String email,
        String address,
        String phone,
        BigDecimal baseSalary,
        String documentNumber,
        String role    
){}
