package org.example.park_ease.controller;

import org.example.park_ease.dto.request.ParkingLotRequestDTO;
import org.example.park_ease.dto.response.ParkingLotResponseDTO;
import org.example.park_ease.service.ParkingLotService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parking-lots")
public class ParkingLotController {

    private final ParkingLotService parkingLotService;

    public ParkingLotController(ParkingLotService parkingLotService) {
        this.parkingLotService = parkingLotService;
    }

    @GetMapping("/my")
    public List<ParkingLotResponseDTO> getMyParkingLots(Authentication authentication) {
        return parkingLotService.getMyParkingLots(authentication.getName());
    }

    @GetMapping("/{lotId}")
    public ParkingLotResponseDTO getParkingLot(@PathVariable int lotId) {
        return parkingLotService.getParkingLotById(lotId);
    }

    @GetMapping
    public List<ParkingLotResponseDTO> getAllParkingLots() {
        return parkingLotService.getAllParkingLots();
    }

    @PostMapping
    public ParkingLotResponseDTO createParkingLot(@RequestBody ParkingLotRequestDTO requestDTO, Authentication authentication) {
        return parkingLotService.createParkingLot(requestDTO, authentication.getName());
    }

    @DeleteMapping("/{lotId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteParkingLot(@PathVariable int lotId, Authentication authentication) {
        parkingLotService.deleteParkingLot(lotId, authentication.getName());
    }

}
