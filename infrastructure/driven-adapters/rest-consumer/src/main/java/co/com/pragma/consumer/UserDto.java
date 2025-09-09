package co.com.pragma.consumer;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UserDto(
        BigDecimal id,
        String firstName,
        String lastName,
        String documentId,
        LocalDate birthDate,
        String address,
        String phone,
        String email,
        BigDecimal salary,
        Integer role
) {

}