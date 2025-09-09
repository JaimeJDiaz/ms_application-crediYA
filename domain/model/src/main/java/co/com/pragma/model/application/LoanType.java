package co.com.pragma.model.application;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanType {
    Long id;
    String name;
    BigDecimal minAmount;
    BigDecimal maxAmount;
    BigDecimal interestRate;
    Boolean autoValidation;
}
