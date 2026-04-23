package com.agoracorp.projectx.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.agoracorp.projectx.dto.AuthRequest;
import com.agoracorp.projectx.dto.AuthResponse;
import com.agoracorp.projectx.model.UserAccount;
import com.agoracorp.projectx.model.UserProfile;
import com.agoracorp.projectx.repository.UserAccountRepository;
import com.agoracorp.projectx.repository.UserProfileRepository;
import com.agoracorp.projectx.security.JwtService;
import com.agoracorp.projectx.security.UserPrincipal;

@Service
public class AuthService {

	private final UserAccountRepository userAccountRepository;
	private final UserProfileRepository userProfileRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	public AuthService(UserAccountRepository userAccountRepository, UserProfileRepository userProfileRepository,
			PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
		this.userAccountRepository = userAccountRepository;
		this.userProfileRepository = userProfileRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

	@Transactional
	public AuthResponse signup(AuthRequest request) {
		String normalizedEmail = request.email().trim().toLowerCase();
		if (userAccountRepository.existsByEmailIgnoreCase(normalizedEmail)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
		}

		UserAccount user = new UserAccount();
		user.setEmail(normalizedEmail);
		user.setPassword(passwordEncoder.encode(request.password()));
		UserAccount savedUser = userAccountRepository.save(user);
		ensureProfileExists(savedUser);

		UserPrincipal principal = new UserPrincipal(savedUser);
		String token = jwtService.generateToken(principal);
		return new AuthResponse(token, "Bearer", savedUser.getId(), savedUser.getEmail());
	}

	@Transactional
	public AuthResponse login(AuthRequest request) {
		String normalizedEmail = request.email().trim().toLowerCase();
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(normalizedEmail, request.password()));

		UserAccount user = userAccountRepository.findByEmailIgnoreCase(normalizedEmail)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

		ensureProfileExists(user);
		UserPrincipal principal = new UserPrincipal(user);
		String token = jwtService.generateToken(principal);
		return new AuthResponse(token, "Bearer", user.getId(), user.getEmail());
	}

	private void ensureProfileExists(UserAccount user) {
		userProfileRepository.findByUserId(user.getId()).ifPresentOrElse(profile -> {
		}, () -> {
			UserProfile profile = new UserProfile();
			profile.setUser(user);
			profile.setFullName(null);
			profile.setBio(null);
			userProfileRepository.save(profile);
		});
	}
}
