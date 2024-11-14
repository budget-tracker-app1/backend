package com.my_sample_project.BudgetApp.controller;

import com.my_sample_project.BudgetApp.model.User;
import com.my_sample_project.BudgetApp.repository.UserRepository;
import com.my_sample_project.BudgetApp.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    // Registration endpoint
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Username already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body("User registered successfully: " + user.getUsername());
    }

    // Login endpoint to generate both access and refresh tokens
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User user) {
        try {
            // Log the username being authenticated
            logger.info("Authenticating user: {}", user.getUsername());

            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );

            // Set the authentication object in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Retrieve authenticated user's details from Spring Security
            UserDetails authenticatedUserDetails = (UserDetails) authentication.getPrincipal();

            // Log the authenticated user's details
            logger.info("Authenticated user: {}", authenticatedUserDetails.getUsername());

            // Load the custom User object from the database
            User authenticatedUser = userRepository.findByUsername(authenticatedUserDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Extract userId and username
            Long userId = authenticatedUser.getId();
            String username = authenticatedUser.getUsername();

            // Generate Access and Refresh tokens
            String accessToken = jwtUtil.generateAccessToken(username, userId);
            String refreshToken = jwtUtil.generateRefreshToken(username, userId);

            // Return the tokens to the client
            return ResponseEntity.ok(Map.of(
                "access_token", accessToken,
                "refresh_token", refreshToken
            ));
        } catch (BadCredentialsException e) {
            // Handle invalid credentials error
            logger.error("Invalid credentials for user: {}", user.getUsername(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid username or password"));
        } catch (Exception e) {
            // Catch all other exceptions
            logger.error("Error occurred during login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An error occurred during login"));
        }
    }

    // Endpoint to refresh the access token using the refresh token
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshAccessToken(@RequestBody String refreshToken) {
        try {
            // Extract the username and user ID from the refresh token
            String username = jwtUtil.extractUsername(refreshToken);
            Long userId = jwtUtil.extractUserId(refreshToken);

            // Validate the token
            if (jwtUtil.validateToken(refreshToken, username)) {
                // Extract the expiration date of the refresh token
                Date expirationDate = jwtUtil.extractExpiration(refreshToken);

                // Check if the refresh token is expired
                if (expirationDate.before(new Date())) {
                    // Return a 401 response if expired
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(Map.of("error", "Refresh token expired"));
                }

                // Generate a new access token
                String newAccessToken = jwtUtil.generateAccessToken(username, userId);
                return ResponseEntity.ok(Map.of("access_token", newAccessToken));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid refresh token"));
            }
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // Token expired, return a 401 Unauthorized response with error message
            System.err.println("JWT Token expired: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Refresh token expired"));
        } catch (Exception e) {
            // Catch all other exceptions
            System.err.println("Error refreshing token: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while refreshing the token"));
        }
    }
}
