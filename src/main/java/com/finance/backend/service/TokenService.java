package com.finance.backend.service;

import com.finance.backend.entity.AppUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final Duration expiration;

    public TokenService(
            JwtEncoder jwtEncoder,
            @Value("${app.security.jwt-issuer}") String issuer,
            @Value("${app.security.jwt-expiration}") Duration expiration
    ) {
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.expiration = expiration;
    }

    public String createToken(AppUser user) {
        Instant issuedAt = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plus(expiration))
                .subject(user.getEmail())
                .claim("name", user.getName())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public long expirationSeconds() {
        return expiration.toSeconds();
    }
}
