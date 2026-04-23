package com.agoracorp.projectx.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.agoracorp.projectx.dto.AuthRequest;
import com.agoracorp.projectx.dto.AuthResponse;
import com.agoracorp.projectx.model.UserAccount;
import com.agoracorp.projectx.model.UserProfile;
import com.agoracorp.projectx.repository.UserAccountRepository;
import com.agoracorp.projectx.repository.UserProfileRepository;
import com.agoracorp.projectx.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UserAccountRepository userAccountRepository;
	@Mock
	private UserProfileRepository userProfileRepository;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private AuthenticationManager authenticationManager;
	@Mock
	private JwtService jwtService;

	private AuthService authService;

	@BeforeEach
	void setUp() {
		authService = new AuthService(userAccountRepository, userProfileRepository, passwordEncoder, authenticationManager,
				jwtService);
	}

	@Test
	void signup_shouldCreateUserAndProfileAndReturnToken() {
		AuthRequest request = new AuthRequest("USER@MAIL.COM", "Password123");
		when(userAccountRepository.existsByEmailIgnoreCase("user@mail.com")).thenReturn(false);
		when(passwordEncoder.encode("Password123")).thenReturn("encoded");
		when(userAccountRepository.save(any(UserAccount.class))).thenAnswer(invocation -> {
			UserAccount user = invocation.getArgument(0);
			user.setId(10L);
			return user;
		});
		when(userProfileRepository.findByUserId(10L)).thenReturn(Optional.empty());
		when(jwtService.generateToken(any())).thenReturn("jwt-token");

		AuthResponse response = authService.signup(request);

		assertEquals("jwt-token", response.token());
		assertEquals("user@mail.com", response.email());
		assertEquals(10L, response.userId());
		verify(userProfileRepository).save(any(UserProfile.class));
	}

	@Test
	void signup_shouldThrowConflict_whenEmailExists() {
		AuthRequest request = new AuthRequest("user@mail.com", "Password123");
		when(userAccountRepository.existsByEmailIgnoreCase("user@mail.com")).thenReturn(true);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> authService.signup(request));

		assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
		verify(userAccountRepository, never()).save(any(UserAccount.class));
	}

	@Test
	void login_shouldAuthenticateAndEnsureProfileExists() {
		AuthRequest request = new AuthRequest("user@mail.com", "Password123");
		UserAccount user = new UserAccount();
		user.setId(8L);
		user.setEmail("user@mail.com");
		user.setPassword("encoded");
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenReturn(new Authentication() {
					@Override
					public String getName() {
						return "user@mail.com";
					}

					@Override
					public void setAuthenticated(boolean isAuthenticated) {
					}

					@Override
					public boolean isAuthenticated() {
						return true;
					}

					@Override
					public Object getPrincipal() {
						return null;
					}

					@Override
					public Object getDetails() {
						return null;
					}

					@Override
					public Object getCredentials() {
						return null;
					}

					@Override
					public java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() {
						return java.util.List.of();
					}
				});
		when(userAccountRepository.findByEmailIgnoreCase("user@mail.com")).thenReturn(Optional.of(user));
		when(userProfileRepository.findByUserId(8L)).thenReturn(Optional.empty());
		when(jwtService.generateToken(any())).thenReturn("jwt");

		AuthResponse response = authService.login(request);

		assertNotNull(response);
		assertEquals("jwt", response.token());
		verify(userProfileRepository).save(any(UserProfile.class));
	}
}
