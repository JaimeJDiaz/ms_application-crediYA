package co.com.pragma.model.application;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Status {
    Integer id;
    String name;
    String description;
}
