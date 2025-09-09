package co.com.pragma.r2dbc.entities;

import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "status")
public class StatusEntity {
    @Id
    private Long id;
    private String name;
    private String description;
}
