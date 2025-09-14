package com.example.web_ai.util;

import com.example.web_ai.entity.User;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Slf4j
public final class JwtUtil {

    private static SecretKey secretKey;
    private static String issuer;
    private static long expirationMinutes;

    private JwtUtil() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    // gọi trong SecurityConfig khi khởi tạo
    public static void init(String secret, String issuer, long expMinutes) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        JwtUtil.issuer = issuer;
        expirationMinutes = expMinutes;
    }

    public static String generateToken(User user) {
        Instant now = Instant.now();

        try {
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .issuer(issuer)
                    .subject(user.getUsername())
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(now.plus(expirationMinutes, ChronoUnit.MINUTES)))
                    .jwtID(UUID.randomUUID().toString())
                    .claim("id", user.getId())
                    .claim("role", user.getRole())
                    .build();

            JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
            SignedJWT jwt = new SignedJWT(header, claims);
            jwt.sign(new MACSigner(secretKey));

            return jwt.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Error generating JWT token", e);
        }
    }

    public static boolean verifyToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();

            boolean isValid = signedJWT.verify(new MACVerifier(secretKey));
            if (!isValid) {
                log.warn("Invalid token");
            } else if (expirationTime.before(new Date())) {
                log.warn("Expired token");
                return false;
            }
            return isValid;
        } catch (Exception e) {
            log.warn("Error verifying token", e);
            return false;
        }
    }

    public static String getUserNameFromToken(String token) {
        try {
            return SignedJWT.parse(token).getJWTClaimsSet().getSubject();
        } catch (Exception e) {
            log.warn("Error extracting username", e);
            return null;
        }
    }

    public static String getJwtIDFromToken(String token) {
        try {
            return SignedJWT.parse(token).getJWTClaimsSet().getJWTID();
        } catch (Exception e) {
            log.warn("Error extracting jwtID", e);
            return null;
        }
    }

    public static Date getExpirationTimeFromToken(String token) {
        try {
            return SignedJWT.parse(token).getJWTClaimsSet().getExpirationTime();
        } catch (Exception e) {
            log.warn("Error extracting expiration time", e);
            return null;
        }
    }

    public static String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
