package co.com.pragma.model.application;

import java.math.BigDecimal;

public class LoanType {
    Integer id;
    String name;
    BigDecimal minAmount;
    BigDecimal maxAmount;
    BigDecimal interestRate;
    Boolean autoValidation;
}
