package com.example.ipl.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret:defaultSecretKey123456789}")
    private String secretKey;
    
    private static final long EXPIRATION_TIME = 3600000; // 1 hour
    public String generateToken(String username)
    {
        return JWT.create() .withSubject(username) .withIssuedAt(new Date()) .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) .sign(Algorithm.HMAC256(secretKey));
    }
    public DecodedJWT verifyToken(String token)
    {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secretKey)).build();
        return verifier.verify(token);
    }
}
