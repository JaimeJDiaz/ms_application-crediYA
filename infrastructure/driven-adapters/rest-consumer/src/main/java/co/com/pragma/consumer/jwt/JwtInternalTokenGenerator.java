package co.com.pragma.consumer.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtInternalTokenGenerator {

    private final SecretKey secretKey;

    public JwtInternalTokenGenerator(@Value("${jwt.secret}") String secret) {

        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateInternalToken() {
        return Jwts.builder()
                .subject("internal-service")
                .claim("role", "ROLE_INTERNAL_SERVICE")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }
}
