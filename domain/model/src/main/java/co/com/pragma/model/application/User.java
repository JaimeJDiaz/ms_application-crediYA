package co.com.pragma.model.application;

import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private BigInteger id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;
    private String salary;
}
