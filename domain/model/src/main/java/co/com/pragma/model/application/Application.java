package co.com.pragma.model.application;
import co.com.pragma.model.application.enums.Status;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
//import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

@Getter
@Setter
//@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Application {
    private BigInteger id;
    private BigDecimal amount;
    private String type;
    private BigInteger userId;
    private Status status;
}
