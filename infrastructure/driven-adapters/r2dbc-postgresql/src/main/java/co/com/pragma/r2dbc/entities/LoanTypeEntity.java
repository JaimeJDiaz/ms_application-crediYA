package co.com.pragma.r2dbc.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "loan_type")
public class LoanTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private BigDecimal minAmount;

    private BigDecimal maxAmount;

    @Column(name = "interest_rate")
    private BigDecimal interestRate;

    @Column(name = "auto_validation")
    private Boolean autoValidation;

}
