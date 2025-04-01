package com.example.demo.security;

import com.example.demo.model.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private UserDetails userDetails;
    private String token;
    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long EXPIRATION = 36000L;
    private Key key;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();


        setField(jwtUtil, "secret", SECRET_KEY);
        setField(jwtUtil, "expiration", EXPIRATION);


        key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());


        userDetails = new User(
                "testuser",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority(Role.USER.name()))
        );


        token = generateToken(userDetails);
    }

    private String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        return jwtUtil.createToken(claims, userDetails.getUsername());
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        String generatedToken = jwtUtil.generateToken(userDetails);

        assertNotNull(generatedToken);

        assertTrue(jwtUtil.validateToken(generatedToken, userDetails));

        String username = jwtUtil.extractUsername(generatedToken);
        assertEquals("testuser", username);
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        String username = jwtUtil.extractUsername(token);

        assertEquals("testuser", username);
    }

    @Test
    void extractExpiration_ShouldReturnCorrectExpiration() {
        Date expiration = jwtUtil.extractExpiration(token);

        assertNotNull(expiration);

        assertTrue(expiration.after(new Date()));
    }

    @Test
    void validateToken_ShouldReturnTrue_ForValidToken() {
        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void validateToken_ShouldReturnFalse_ForExpiredToken() {
        String expiredToken = Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60))
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 30))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        assertFalse(jwtUtil.validateToken(expiredToken, userDetails));
    }


    @Test
    void extractRoles_ShouldReturnGrantedAuthorities() {
        var roles = jwtUtil.extractRoles(token);

        assertEquals(1, roles.size());
        assertEquals("USER", roles.get(0).getAuthority());
    }

    @Test
    void generateToken_ShouldCreateDifferentTokensForDifferentUsers() {
        UserDetails otherUser = new User(
                "otheruser",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("USER"))
        );

        String token1 = jwtUtil.generateToken(userDetails);
        String token2 = jwtUtil.generateToken(otherUser);

        assertNotEquals(token1, token2);

        assertEquals("testuser", jwtUtil.extractUsername(token1));
        assertEquals("otheruser", jwtUtil.extractUsername(token2));
    }
}
