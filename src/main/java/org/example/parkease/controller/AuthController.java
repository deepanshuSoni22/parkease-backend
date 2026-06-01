package org.example.parkease.controller;

import org.example.parkease.dto.request.UserRequestDTO;
import org.example.parkease.dto.response.UserResponseDTO;
import org.example.parkease.entity.User;
import org.example.parkease.repository.UserRepository;
import org.example.parkease.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    private final UserRepository userRepository;

    public AuthController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public UserResponseDTO me(Authentication authentication) {

        User user = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow();

        UserResponseDTO dto = new UserResponseDTO();

        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());

        return dto;
    }

    @PostMapping("/register")
    public UserResponseDTO register(@RequestBody UserRequestDTO requestDTO) {
        return userService.register(requestDTO);
    }


}
