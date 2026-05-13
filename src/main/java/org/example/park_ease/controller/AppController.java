package org.example.park_ease.controller;

import org.example.park_ease.dto.request.ParkingLotRequestDTO;
import org.example.park_ease.dto.request.UserRequestDTO;
import org.example.park_ease.dto.response.ParkingLotResponseDTO;
import org.example.park_ease.dto.response.UserResponseDTO;
import org.example.park_ease.service.ParkingLotService;
import org.example.park_ease.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AppController {

    private final UserService userService;
    private final ParkingLotService parkingLotService;

    public AppController(UserService userService, ParkingLotService parkingLotService) {
        this.userService = userService;
        this.parkingLotService = parkingLotService;
    }

    @PostMapping("/register")
    public UserResponseDTO register(@RequestBody UserRequestDTO requestDTO) {
        return userService.register(requestDTO);
    }

    @GetMapping("/")
    public String home(Authentication authentication) {
        return "Welcome, " + authentication.getName() + " 🙋‍♂️";
    }

    @PostMapping("/create-parking-lot")
    public ParkingLotResponseDTO createParkingLot(@RequestBody ParkingLotRequestDTO requestDTO) {
        return parkingLotService.createParkingLot(requestDTO);
    }

    @GetMapping("/my-parking-lots")
    public List<ParkingLotResponseDTO> getMyParkingLots(Authentication authentication) {
        return parkingLotService.getMyParkingLots(authentication.getName());
    }

// ---------------------------------------------------------------------------------------------

    // Admin specific routes
    @GetMapping("/all-users")
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/all-parking-lots")
    public List<ParkingLotResponseDTO> listAllParkingLots() {
        return parkingLotService.getAllParkingLots();
    }
}
