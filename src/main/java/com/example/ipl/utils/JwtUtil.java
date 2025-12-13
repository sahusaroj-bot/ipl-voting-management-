package com.example.ipl.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret:}")
    private String secretKey;
    
    @Value("${jwt.refresh.secret:}")
    private String refreshSecretKey;
    
    private static final long ACCESS_TOKEN_EXPIRATION = 900000; // 15 minutes
    private static final long REFRESH_TOKEN_EXPIRATION = 604800000; // 7 days
    
    private String getSecretKey() {
        if (secretKey == null || secretKey.isEmpty()) {
            secretKey = generateSecureKey();
        }
        return secretKey;
    }
    
    private String getRefreshSecretKey() {
        if (refreshSecretKey == null || refreshSecretKey.isEmpty()) {
            refreshSecretKey = generateSecureKey();
        }
        return refreshSecretKey;
    }
    
    private String generateSecureKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[64];
        secureRandom.nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }
    
    public String generateToken(String username) {
        return JWT.create()
            .withSubject(username)
            .withIssuedAt(new Date())
            .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
            .withClaim("type", "access")
            .sign(Algorithm.HMAC256(getSecretKey()));
    }
    
    public String generateRefreshToken(String username) {
        return JWT.create()
            .withSubject(username)
            .withIssuedAt(new Date())
            .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
            .withClaim("type", "refresh")
            .sign(Algorithm.HMAC256(getRefreshSecretKey()));
    }
    
    public DecodedJWT verifyToken(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(getSecretKey()))
            .withClaim("type", "access")
            .build();
        return verifier.verify(token);
    }
    
    public DecodedJWT verifyRefreshToken(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(getRefreshSecretKey()))
            .withClaim("type", "refresh")
            .build();
        return verifier.verify(token);
    }
    
    public String extractUsername(String token) {
        try {
            DecodedJWT decodedJWT = verifyToken(token);
            return decodedJWT.getSubject();
        } catch (JWTVerificationException e) {
            return null;
        }
    }
    
    public boolean validateToken(String token, String username) {
        try {
            String extractedUsername = extractUsername(token);
            return extractedUsername != null && extractedUsername.equals(username);
        } catch (Exception e) {
            return false;
        }
    }
}
