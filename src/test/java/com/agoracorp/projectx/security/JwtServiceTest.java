package com.agoracorp.projectx.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.agoracorp.projectx.model.UserAccount;

class JwtServiceTest {

	@Test
	void shouldGenerateAndValidateToken() {
		String secret = "this-is-a-very-long-secret-key-with-more-than-thirty-two-bytes";
		JwtService jwtService = new JwtService(secret, 60);

		UserAccount user = new UserAccount();
		user.setId(1L);
		user.setEmail("user@mail.com");
		user.setPassword("encoded");
		UserPrincipal principal = new UserPrincipal(user);

		String token = jwtService.generateToken(principal);

		assertTrue(jwtService.isTokenValid(token, principal));
		assertEquals("user@mail.com", jwtService.extractUsername(token));
	}
}
