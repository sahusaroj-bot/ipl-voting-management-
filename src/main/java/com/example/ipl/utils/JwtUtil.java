package com.example.ipl.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import java.util.Date;

public class JwtUtil {
    private static final String SECRET_KEY = "1221212";
    private static final long EXPIRATION_TIME = 360000; // 1 hour
    public static String generateToken(String username)
    {
        return JWT.create() .withSubject(username) .withIssuedAt(new Date()) .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) .sign(Algorithm.HMAC256(SECRET_KEY));
    }
    public static DecodedJWT verifyToken(String token)
    {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET_KEY)).build();
        return verifier.verify(token);
    }
}
