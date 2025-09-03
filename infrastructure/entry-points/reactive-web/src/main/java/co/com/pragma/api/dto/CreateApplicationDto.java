package co.com.pragma.api.dto;

import co.com.pragma.model.application.enums.Type;

import java.math.BigDecimal;
import java.math.BigInteger;

public record CreateApplicationDto(BigDecimal amount, Type type, BigInteger userId) {
}
