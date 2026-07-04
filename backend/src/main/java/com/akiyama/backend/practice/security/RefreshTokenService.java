package com.akiyama.backend.practice.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private final JwtService jwtService;

    // Refresh Token生成
    public String generatedRefreshToken(UserDetails userDetails) {

        Date now = new Date();
        Date exp = new Date(now.getTime() + refreshExpiration);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(exp)
                .signWith(jwtService.getSigningKey())
                .compact();
    }

    // Refresh Token検証
    public boolean isValid(String token, UserDetails userDetails) {
        
        String username = jwtService.extractUsername(token);

        return username.equals(userDetails.getUsername())
                && !isExpired(token);
    }

    private boolean isExpired(String token) {
        return jwtService
                    .extractClaim(token, claims -> claims.getExpiration())
                    .before(new Date());
    }
}
