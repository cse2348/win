package com.example.win.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "MySuperSecretKeyForJWTMySuperSecretKeyForJWT";
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 1일

    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public String createToken(String phoneNumber) {
        return Jwts.builder()
                .setSubject(phoneNumber)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            System.out.println("JWT 유효성 검사 실패: " + e.getMessage());
            return false;
        }
    }

    public String getPhoneNumberFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
