package org.example.park_ease.service;

import org.example.park_ease.dto.request.ParkingSlotRequestDTO;
import org.example.park_ease.dto.response.ParkingSlotResponseDTO;
import org.example.park_ease.entity.ParkingLot;
import org.example.park_ease.entity.ParkingSlot;
import org.example.park_ease.entity.User;
import org.example.park_ease.repository.ParkingLotRepository;
import org.example.park_ease.repository.ParkingSlotRepository;
import org.example.park_ease.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ParkingSlotService {

    private final ParkingSlotRepository parkingSlotRepository;
    private final ParkingLotRepository parkingLotRepository;
    private final UserRepository userRepository;

    public ParkingSlotService(ParkingSlotRepository parkingSlotRepository, ParkingLotRepository parkingLotRepository, UserRepository userRepository) {
        this.parkingSlotRepository = parkingSlotRepository;
        this.parkingLotRepository = parkingLotRepository;
        this.userRepository = userRepository;
    }

    public ParkingSlotResponseDTO createParkingSlot(ParkingSlotRequestDTO requestDTO) {

        // get logged-in username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));


        ParkingLot parkingLot = parkingLotRepository.findByOwner(user)
                .orElseThrow(() -> new RuntimeException("Parking Lot Not Found!!"));


        // DTO -> Entity mapping
        ParkingSlot parkingSlot = new ParkingSlot();

        // backend-controller
        parkingSlot.setParkingLot(parkingLot);

        parkingSlot.setSlotNumber(requestDTO.getSlotNumber());
        parkingSlot.setSlotType(requestDTO.getSlotType());
        parkingSlot.setIsAvailable(requestDTO.getIsAvailable());


        // Saving to Database
        parkingSlotRepository.save(parkingSlot);

        // Entity -> DTO
        ParkingSlotResponseDTO dto = new ParkingSlotResponseDTO();

        dto.setSlotNumber(parkingSlot.getSlotNumber());
        dto.setSlotType(parkingSlot.getSlotType());
        dto.setIsAvailable(parkingSlot.getIsAvailable());

        return dto;
    }




}
