package co.com.pragma.r2dbc.entities;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.math.BigInteger;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "applications")
public class ApplicationEntity {

    @Id
    private BigInteger id;

    private BigDecimal amount;

    @Column("loan_type_id")
    private Long type;

    @Column("user_id")
    private BigInteger userId;

    @Column("status_id")
    private Long status;

    private Integer term;

}
