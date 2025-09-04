package co.com.pragma.r2dbc.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.math.BigInteger;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "applications")
public class ApplicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    private BigDecimal amount;

    @Column(name = "loan_type_id")
    private Integer type;

    @Column(name = "user_id")
    private BigInteger userId;

    @Column(name = "status_id")
    private Integer status;

    private Integer term;

}
