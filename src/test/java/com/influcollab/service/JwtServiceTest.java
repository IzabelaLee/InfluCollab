package com.influcollab.service;

import com.influcollab.enums.UserRole;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET = "this-is-a-test-secret-key-that-is-long-enough-for-hs256";

    private static final long EXPIRATION = 86_400_000L;


    @BeforeEach
    void setUp() {

        jwtService = new JwtService();

        ReflectionTestUtils.setField(jwtService, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", EXPIRATION);
    }

    @Test
    void shouldGenerateValidToken() {

        String token = jwtService.generateToken(1L, "john@example.com", UserRole.USER_ROLE);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void shouldExtractEmailFromToken() {

        String token = jwtService.generateToken(1L, "john@example.com", UserRole.USER_ROLE);
        String email = jwtService.extractEmail(token);

        assertEquals("john@example.com", email);
    }

    @Test
    void shouldExtractUserIdFromToken() {

        String token = jwtService.generateToken(123L, "john@example.com", UserRole.USER_ROLE);

        Long userId = jwtService.extractUserId(token);

        assertEquals(123L, userId);
    }

    @Test
    void shouldExtractRoleFromToken() {

        String token = jwtService.generateToken(1L, "john@example.com", UserRole.USER_ROLE);
        String role = jwtService.extractRole(token);

        assertEquals(UserRole.USER_ROLE.name(), role);
    }

    @Test
    void shouldGenerateTokenWithCorrectClaims() {

        String token = jwtService.generateToken(42L, "john@example.com", UserRole.USER_ROLE);

        assertEquals("john@example.com", jwtService.extractEmail(token));
        assertEquals(42L, jwtService.extractUserId(token));
        assertEquals("USER_ROLE", jwtService.extractRole(token));
    }

    @Test
    void shouldRejectInvalidToken() {

        assertFalse(jwtService.isTokenValid("invalid-token"));
    }

    @Test
    void shouldRejectTamperedToken() {

        String token = jwtService.generateToken(1L, "john@example.com", UserRole.USER_ROLE);
        String tamperedToken = token.substring(0, token.length() - 2) + "xx";

        assertFalse(jwtService.isTokenValid(tamperedToken));
    }

    @Test
    void shouldRejectTokenSignedWithDifferentSecret() {

        String differentSecret = "another-test-secret-key-that-is-long-enough-for-hs256";

        SecretKey differentKey = Keys.hmacShaKeyFor(differentSecret.getBytes(StandardCharsets.UTF_8));

        String token =
                Jwts.builder()
                        .setSubject("john@example.com")
                        .claim("userId", 1L)
                        .claim("role", UserRole.USER_ROLE)
                        .setIssuedAt(new Date())
                        .setExpiration(
                                new Date(
                                        System.currentTimeMillis()
                                                + EXPIRATION
                                )
                        )
                        .signWith(
                                differentKey,
                                SignatureAlgorithm.HS256
                        )
                        .compact();

        assertFalse(jwtService.isTokenValid(token));
    }

    @Test
    void shouldRejectExpiredToken() {

        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

        String expiredToken =
                Jwts.builder()
                        .setSubject("john@example.com")
                        .claim("userId", 1L)
                        .claim("role", UserRole.USER_ROLE)
                        .setIssuedAt(
                                new Date(
                                        System.currentTimeMillis() - 10_000
                                )
                        )
                        .setExpiration(
                                new Date(
                                        System.currentTimeMillis() - 5_000
                                )
                        )
                        .signWith(
                                key,
                                SignatureAlgorithm.HS256
                        )
                        .compact();

        assertFalse(jwtService.isTokenValid(expiredToken));
    }

    @Test
    void shouldSetExpirationOnGeneratedToken() {

        String token = jwtService.generateToken(1L, "john@example.com", UserRole.USER_ROLE);
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration();

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }
}
