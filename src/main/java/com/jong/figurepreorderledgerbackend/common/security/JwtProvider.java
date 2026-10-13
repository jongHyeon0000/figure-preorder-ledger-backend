package com.jong.figurepreorderledgerbackend.common.security;

import com.jong.figurepreorderledgerbackend.config.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtProvider {

    private static final String ISSUER = "figure-preorder-ledger";

    private final SecretKey secretKey;
    private final long expirationMillis;
    private final JwtParser jwtParser;

    public JwtProvider(JwtProperties jwtProperties) {
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = TimeUnit.HOURS.toMillis(jwtProperties.getExpirationHours());

        this.jwtParser = Jwts.parser()
                .verifyWith(secretKey)
                .requireIssuer(ISSUER)
                .build();
    }

    public String createToken(Long userId) {
        Date now = new Date();
        return Jwts.builder()
                .issuer(ISSUER)
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMillis))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public Long getUserId(String token) {
        Claims claims = jwtParser.parseSignedClaims(token).getPayload();
        return Long.valueOf(claims.getSubject());
    }
}
