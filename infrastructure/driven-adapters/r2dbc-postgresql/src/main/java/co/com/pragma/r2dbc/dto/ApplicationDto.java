package co.com.pragma.r2dbc.dto;

import java.math.BigDecimal;
import java.math.BigInteger;

public record ApplicationDto(BigInteger id,
                             BigDecimal amount,
                             Long type,
                             BigInteger userId,
                             Long status,
                             Integer term
                             ) {
}
