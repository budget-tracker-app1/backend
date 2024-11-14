package com.my_sample_project.BudgetApp.service;

import com.my_sample_project.BudgetApp.dto.api.ApiResponseDTO;
import com.my_sample_project.BudgetApp.dto.error.ErrorResponseDTO;
import com.my_sample_project.BudgetApp.dto.user.UserDTO;
import com.my_sample_project.BudgetApp.model.User;
import com.my_sample_project.BudgetApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ApiResponseDTO registerUser(UserDTO userDTO) {
        // Check if the username already exists
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            // Use ErrorResponseDTO for structured error response
            return new ErrorResponseDTO(
                "Username already taken.",
                400,
                "Bad Request"
            );
        }

        // Convert DTO to entity
        User user = new User();
        user.setUsername(userDTO.getUsername());

        // Encode the password before saving
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        // Save the user
        userRepository.save(user);

        // Use ApiResponseDTO for structured success response
        return new ApiResponseDTO(
            true,
            "User registered successfully."
        );
    }
}
