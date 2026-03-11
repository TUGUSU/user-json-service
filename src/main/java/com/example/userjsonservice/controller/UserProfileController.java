package com.example.userjsonservice.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.userjsonservice.model.UserProfile;
import com.example.userjsonservice.service.TokenValidationService;

@RestController
@RequestMapping("/users")
public class UserProfileController {

	private final Map<Integer, UserProfile> profiles = new HashMap<>();
	private final TokenValidationService tokenValidationService;

	public UserProfileController(TokenValidationService tokenValidationService) {
		this.tokenValidationService = tokenValidationService;
		profiles.put(1, new UserProfile(1, "Bat-Erdene", "bat@example.com", "SOA student", "99112233"));
	}

	private boolean isUnauthorized(String authHeader) {
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return true;
		}

		String token = authHeader.substring(7);
		return !tokenValidationService.validateToken(token);
	}

	@PostMapping
	public ResponseEntity<?> createUser(@RequestHeader(value = "Authorization", required = false) String authHeader,
			@RequestBody UserProfile profile) {
		if (isUnauthorized(authHeader)) {
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}

		profiles.put(profile.getId(), profile);
		return new ResponseEntity<>(profile, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getUser(@RequestHeader(value = "Authorization", required = false) String authHeader,
			@PathVariable int id) {
		if (isUnauthorized(authHeader)) {
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}

		UserProfile profile = profiles.get(id);
		if (profile != null) {
			return new ResponseEntity<>(profile, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateUser(@RequestHeader(value = "Authorization", required = false) String authHeader,
			@PathVariable int id, @RequestBody UserProfile updatedProfile) {
		if (isUnauthorized(authHeader)) {
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}

		if (profiles.containsKey(id)) {
			updatedProfile.setId(id);
			profiles.put(id, updatedProfile);
			return new ResponseEntity<>(updatedProfile, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUser(@RequestHeader(value = "Authorization", required = false) String authHeader,
			@PathVariable int id) {
		if (isUnauthorized(authHeader)) {
			return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}

		if (profiles.containsKey(id)) {
			profiles.remove(id);
			return new ResponseEntity<>("User deleted successfully.", HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
}