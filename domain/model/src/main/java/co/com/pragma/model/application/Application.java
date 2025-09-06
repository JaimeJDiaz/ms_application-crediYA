package co.com.pragma.model.application;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Application {
    private BigInteger id;
    private BigDecimal amount;
    private Integer type;
    private BigInteger userId;
    private Integer status;
    private Integer term;
}
