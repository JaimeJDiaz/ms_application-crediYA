package co.com.pragma.api.dto;

import co.com.pragma.model.application.enums.Status;
import co.com.pragma.model.application.enums.Type;

import java.math.BigDecimal;
import java.math.BigInteger;

public record ResponseAplicationDto(BigInteger id, BigDecimal amount, Type type, BigInteger userId, Status status) {
}
