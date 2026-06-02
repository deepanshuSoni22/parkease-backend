package org.example.parkease.controller;

import org.example.parkease.dto.response.ParkingLotResponseDTO;
import org.example.parkease.dto.response.UserResponseDTO;
import org.example.parkease.service.ParkingLotService;
import org.example.parkease.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final UserService userService;
    private final ParkingLotService parkingLotService;

    public AdminController(UserService userService, ParkingLotService parkingLotService) {
        this.userService = userService;
        this.parkingLotService = parkingLotService;
    }

    @GetMapping("/users")
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{userId}")
    public UserResponseDTO getUser(@PathVariable int userId) {
        return userService.getUser(userId);
    }

    @GetMapping("/parking-lots")
    public List<ParkingLotResponseDTO> listAllParkingLots() {
        return parkingLotService.getAllParkingLots();
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable int userId) {
        userService.deleteUser(userId);
    }

}
