package org.example.park_ease.service;

import org.example.park_ease.dto.UserResponseDto;
import org.example.park_ease.entity.User;
import org.example.park_ease.exception.UserAlreadyExistsException;
import org.example.park_ease.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserResponseDto addUser(User user) {

        if (user == null) {
            return null;
        }

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        UserResponseDto dto = new UserResponseDto();
        dto.setUsername(user.getUsername());

        return dto;

    }

}
