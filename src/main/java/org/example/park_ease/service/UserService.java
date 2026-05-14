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


    public UserResponseDTO register(UserRequestDTO requestDTO) {

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
        user.setRole(requestDTO.getRole());

        // save to Database
        userRepository.save(user);

        // Entity -> DTO
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(requestDTO.getUsername());
        dto.setRole(requestDTO.getRole());

        return dto;

    }

    public List<UserResponseDTO> getAllUsers() {

        List<User> users = userRepository.findAll();

        // Entity -> DTO mapping
        return users.stream()
                .map(user -> {
                    UserResponseDTO dto = new UserResponseDTO();

                    dto.setId(user.getId());
                    dto.setUsername(user.getUsername());
                    dto.setRole(user.getRole());

                    return dto;
                })
                .toList();
    }

    public UserResponseDTO getUser(int id) {

        User user = userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("User not found!")
        );

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());


        return dto;
    }

    public void deleteUser(int id) {

        User user = userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("User not found!")
        );

        userRepository.delete(user);
    }
}
