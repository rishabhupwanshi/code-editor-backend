package com.project.codeEditor.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.codeEditor.dto.AuthResponse;
import com.project.codeEditor.dto.LoginRequest;
import com.project.codeEditor.dto.RegisterRequest;
import com.project.codeEditor.entity.User;
import com.project.codeEditor.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return new AuthResponse("User already exists", null, request.getEmail(), request.getName());
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setToken(UUID.randomUUID().toString());

        userRepository.save(user);

        return new AuthResponse("Registration Successful", user.getToken(), user.getEmail(), user.getName());
    }

    public AuthResponse login(LoginRequest request) {

        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            return new AuthResponse("User not found", null, request.getEmail(), null);
        }

        User user = userOptional.get();

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            if (user.getToken() == null || user.getToken().isBlank()) {
                user.setToken(UUID.randomUUID().toString());
                userRepository.save(user);
            }

            return new AuthResponse("Login Successful", user.getToken(), user.getEmail(), user.getName());
        }

        return new AuthResponse("Invalid Password", null, request.getEmail(), null);
    }
}