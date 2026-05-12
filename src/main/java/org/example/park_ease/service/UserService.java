package org.example.park_ease.service;

import org.example.park_ease.dto.request.UserRequestDTO;
import org.example.park_ease.dto.response.UserResponseDTO;
import org.example.park_ease.entity.User;
import org.example.park_ease.exception.UserAlreadyExistsException;
import org.example.park_ease.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public UserResponseDTO addUser(UserRequestDTO requestDTO) {

        if (requestDTO == null) {
            return null;
        }

        if (userRepository.findByUsername(requestDTO.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists!");
        }

        // DTO -> Entity Mapping
        User user = new User();
        user.setUsername(requestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));

        // save to Database
        userRepository.save(user);

        // Entity -> DTO
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUsername(requestDTO.getUsername());

        return dto;

    }

    public List<UserResponseDTO> getAllUsers() {

        List<User> users = userRepository.findAll();

        // Entity -> DTO mapping
        return users.stream()
                .map(user -> {
                    UserResponseDTO dto = new UserResponseDTO();
                    dto.setUsername(user.getUsername());
                    return dto;
                })
                .toList();
    }

}
