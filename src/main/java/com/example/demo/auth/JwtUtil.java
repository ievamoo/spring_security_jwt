package com.example.demo.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility class for handling JWT (JSON Web Token) operations.
 * This class provides methods for token generation, validation, and extraction of claims.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * Generates a signing key from the secret string.
     * This key is used for both signing and verifying JWT tokens.
     *
     * @return Key object for JWT signing and verification
     */
    private final Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extracts the username (subject) from a JWT token.
     *
     * @param token JWT token string
     * @return username stored in the token's subject claim
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from a JWT token.
     *
     * @param token JWT token string
     * @return Date when the token expires
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Generic method to extract any claim from a JWT token using a claims resolver function.
     *
     * @param token JWT token string
     * @param claimsResolver Function to extract specific claim from Claims object
     * @param <T> Type of the claim to extract
     * @return Extracted claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from a JWT token.
     * This method parses the token and returns the Claims object containing all token claims.
     *
     * @param token JWT token string
     * @return Claims object containing all token claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Checks if a JWT token has expired.
     *
     * @param token JWT token string
     * @return true if the token has expired, false otherwise
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Generates a new JWT token for a user.
     * The token includes the user's roles as claims and is signed with the application's secret key.
     *
     * @param userDetails UserDetails object containing user information
     * @return Generated JWT token string
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Creates a JWT token with the specified claims and subject.
     * Sets the token's issue time and expiration time.
     *
     * @param claims Map of claims to include in the token
     * @param subject Subject (usually username) of the token
     * @return Generated JWT token string
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates a JWT token against user details.
     * Checks if the token's subject matches the username and if the token hasn't expired.
     *
     * @param token JWT token string to validate
     * @param userDetails UserDetails object to validate against
     * @return true if the token is valid, false otherwise
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Extracts roles from a JWT token and converts them to GrantedAuthority objects.
     * This method is used to reconstruct the user's authorities from the token.
     *
     * @param token JWT token string
     * @return List of GrantedAuthority objects representing the user's roles
     */
    public List<GrantedAuthority> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        List<String> roleNames = claims.get("roles", List.class);
        return roleNames.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}