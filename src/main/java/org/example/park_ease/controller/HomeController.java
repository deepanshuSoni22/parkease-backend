package org.example.park_ease.controller;

import org.example.park_ease.dto.UserResponseDto;
import org.example.park_ease.entity.User;
import org.example.park_ease.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public UserResponseDto register(@RequestBody User user) {
        return userService.addUser(user);
    }

    @GetMapping("/")
    public String home(Authentication authentication) {
        return "Welcome, " + authentication.getName() + " 🙋‍♂️";
    }

}
