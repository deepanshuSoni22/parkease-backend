package org.example.park_ease.controller;

import org.example.park_ease.dto.request.ParkingLotRequestDTO;
import org.example.park_ease.dto.response.ParkingLotResponseDTO;
import org.example.park_ease.dto.response.ParkingSlotResponseDTO;
import org.example.park_ease.service.ParkingLotService;
import org.example.park_ease.service.ParkingSlotService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parking-lots")
public class ParkingLotController {

    private final ParkingLotService parkingLotService;
    private final ParkingSlotService parkingSlotService;

    public ParkingLotController(ParkingLotService parkingLotService, ParkingSlotService parkingSlotService) {
        this.parkingLotService = parkingLotService;
        this.parkingSlotService = parkingSlotService;
    }

    @GetMapping("/my")
    public List<ParkingLotResponseDTO> getMyParkingLots(Authentication authentication) {
        return parkingLotService.getMyParkingLots(authentication.getName());
    }

    @GetMapping("/{lotId}")
    public ParkingLotResponseDTO getParkingLot(@PathVariable int lotId) {
        return parkingLotService.getParkingLotById(lotId);
    }

    @GetMapping("/{lotId}/slots")
    public List<ParkingSlotResponseDTO> getParkingSlotsByLot(@PathVariable int lotId) {
        return parkingSlotService.getParkingSlotsByLotId(lotId);
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
