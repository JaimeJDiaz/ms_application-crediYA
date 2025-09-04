package co.com.pragma.api.dto;

import java.math.BigDecimal;
import java.math.BigInteger;

public record UpdateApplicationDto(
        BigInteger id,
        BigDecimal amount,
        Integer type,
        BigInteger userId,
        Integer status,
        Integer term) {
}
