package com.agoracorp.projectx.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class JwtService {

	private final SecretKey secretKey;
	private final long expirationMinutes;

	public JwtService(@Value("${app.security.jwt.secret}") String secret,
			@Value("${app.security.jwt.expiration-minutes:60}") long expirationMinutes) {
		byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
		if (secretBytes.length < 32) {
			secretBytes = Jwts.SIG.HS256.key().build().getEncoded();
		}
		this.secretKey = new SecretKeySpec(secretBytes, "HmacSHA256");
		this.expirationMinutes = expirationMinutes;
	}

	public String generateToken(UserPrincipal principal) {
		Instant now = Instant.now();
		Instant expiry = now.plusSeconds(expirationMinutes * 60);

		return Jwts.builder()
				.subject(principal.getEmail())
				.claim("uid", principal.getId())
				.issuedAt(Date.from(now))
				.expiration(Date.from(expiry))
				.signWith(secretKey)
				.compact();
	}

	public String extractUsername(String token) {
		return parseClaims(token).getSubject();
	}

	public boolean isTokenValid(String token, UserPrincipal principal) {
		Claims claims = parseClaims(token);
		String username = claims.getSubject();
		Date expiration = claims.getExpiration();
		return username.equalsIgnoreCase(principal.getEmail()) && expiration.after(new Date());
	}

	private Claims parseClaims(String token) {
		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
	}
}
