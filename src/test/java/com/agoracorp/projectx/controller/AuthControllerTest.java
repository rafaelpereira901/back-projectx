package com.agoracorp.projectx.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.agoracorp.projectx.dto.AuthRequest;
import com.agoracorp.projectx.dto.AuthResponse;
import com.agoracorp.projectx.service.AuthService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

	@Mock
	private AuthService authService;

	private AuthController authController;

	@BeforeEach
	void setUp() {
		authController = new AuthController(authService);
	}

	@Test
	void signup_shouldReturnCreated() {
		AuthRequest request = new AuthRequest("user@mail.com", "Password123");
		AuthResponse responseDto = new AuthResponse("token", "Bearer", 1L, "user@mail.com");
		when(authService.signup(request)).thenReturn(responseDto);

		ResponseEntity<AuthResponse> response = authController.signup(request);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals("token", response.getBody().token());
	}

	@Test
	void login_shouldReturnOk() {
		AuthRequest request = new AuthRequest("user@mail.com", "Password123");
		AuthResponse responseDto = new AuthResponse("token", "Bearer", 1L, "user@mail.com");
		when(authService.login(request)).thenReturn(responseDto);

		ResponseEntity<AuthResponse> response = authController.login(request);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("user@mail.com", response.getBody().email());
	}
}
