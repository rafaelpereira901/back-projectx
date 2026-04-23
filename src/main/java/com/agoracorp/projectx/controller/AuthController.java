package com.agoracorp.projectx.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agoracorp.projectx.dto.AuthRequest;
import com.agoracorp.projectx.dto.AuthResponse;
import com.agoracorp.projectx.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/signup")
	public ResponseEntity<AuthResponse> signup(@Valid @RequestBody AuthRequest request) {
		return new ResponseEntity<>(authService.signup(request), HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
		return new ResponseEntity<>(authService.login(request), HttpStatus.OK);
	}
}
