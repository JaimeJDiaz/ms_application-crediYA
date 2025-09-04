package co.com.pragma.api.dto;

import java.math.BigDecimal;
import java.math.BigInteger;

public record CreateApplicationDto(
        BigDecimal amount,
        Integer type,
        BigInteger userId,
        Integer term) {
}
