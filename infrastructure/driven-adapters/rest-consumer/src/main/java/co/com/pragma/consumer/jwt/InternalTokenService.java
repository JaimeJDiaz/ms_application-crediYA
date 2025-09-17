package co.com.pragma.consumer.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class InternalTokenService {
    private final JwtInternalTokenGenerator tokenGenerator;


    public String getInternalToken() {
        return tokenGenerator.generateInternalToken();
    }
}

