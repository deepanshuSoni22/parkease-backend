package org.example.park_ease.controller;

import org.example.park_ease.dto.request.UserRequestDTO;
import org.example.park_ease.dto.response.UserResponseDTO;
import org.example.park_ease.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserResponseDTO register(@RequestBody UserRequestDTO requestDTO) {
        return userService.register(requestDTO);
    }

}
