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
    private Long type;
    private BigInteger userId;
    private Long status;
    private Integer term;
}
