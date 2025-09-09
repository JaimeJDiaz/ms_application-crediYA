package co.com.pragma.api.dto;

import java.math.BigDecimal;

public record CreateApplicationDto(
        BigDecimal amount,
        Integer type,
        String userIdentification,
        Integer term) {
}
