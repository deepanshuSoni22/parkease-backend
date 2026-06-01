package org.example.parkease.controller;

import org.example.parkease.dto.request.ParkingSlotRequestDTO;
import org.example.parkease.dto.response.ParkingSlotResponseDTO;
import org.example.parkease.service.ParkingSlotService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parking-slots")
public class ParkingSlotController {

    private final ParkingSlotService parkingSlotService;

    public ParkingSlotController(ParkingSlotService parkingSlotService) {
        this.parkingSlotService = parkingSlotService;
    }

    @GetMapping
    public List<ParkingSlotResponseDTO> getAllParkingSlots() {
        return parkingSlotService.getAllParkingSlots();
    }

    @GetMapping("/{slotId}")
    public ParkingSlotResponseDTO getParkingSlotById(@PathVariable int slotId) {
        return parkingSlotService.getParkingSlotById(slotId);
    }

    @PostMapping
    public ParkingSlotResponseDTO createParkingSlot(@RequestBody ParkingSlotRequestDTO requestDTO, Authentication authentication) {
        return parkingSlotService.createParkingSlot(requestDTO, authentication.getName());
    }

    @DeleteMapping("/{slotId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteParkingSlotById(@PathVariable int slotId, Authentication authentication) {
        parkingSlotService.deleteParkingSlotById(slotId, authentication.getName());
    }

}
