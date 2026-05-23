package com.job_sap.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import io.jsonwebtoken.Claims;

@Component
public class JwtUtil {

	// Inject the secret key from your environment variables
	@Value("${jwt.secret}")
	private String secretString;

	// Convert the string to a key
	private Key getSigningKey() {
		byte[] keyBytes = secretString.getBytes();
		return Keys.hmacShaKeyFor(keyBytes);
	}

	private final long EXPIRATION_TIME = 86400000;

	public String generateToken(String email, String role) {
		return Jwts.builder().setSubject(email).claim("role", role).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256) // Use the fixed key
				.compact();
	}

	public Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()) // Use the fixed key
				.build().parseClaimsJws(token).getBody();
	}

	public String extractEmail(String token) {
		return extractAllClaims(token).getSubject();
	}

	public String extractRole(String token) {
		return (String) extractAllClaims(token).get("role");
	}

	public boolean isTokenValid(String token) {
		return extractAllClaims(token).getExpiration().after(new Date());
	}
}