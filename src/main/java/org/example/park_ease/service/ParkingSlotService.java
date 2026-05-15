package org.example.park_ease.service;

import org.example.park_ease.dto.request.ParkingSlotRequestDTO;
import org.example.park_ease.dto.response.ParkingSlotResponseDTO;
import org.example.park_ease.entity.ParkingLot;
import org.example.park_ease.entity.ParkingSlot;
import org.example.park_ease.entity.User;
import org.example.park_ease.repository.ParkingLotRepository;
import org.example.park_ease.repository.ParkingSlotRepository;
import org.example.park_ease.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public ParkingSlotResponseDTO getParkingSlotById(int id) {
        ParkingSlot parkingSlot = parkingSlotRepository.findById(id).orElseThrow(
                () -> new RuntimeException("ParkingSlot Not Found!")
        );

        return mapToResponseDTO(parkingSlot);
    }

    public List<ParkingSlotResponseDTO> getAllParkingSlots() {

        return parkingSlotRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    public ParkingSlotResponseDTO createParkingSlot(ParkingSlotRequestDTO requestDTO, String username) {

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
        parkingSlot.setAvailable(requestDTO.getIsAvailable());


        // Saving to Database
        parkingSlotRepository.save(parkingSlot);

        // Entity -> DTO
        ParkingSlotResponseDTO dto = new ParkingSlotResponseDTO();

        dto.setSlotNumber(parkingSlot.getId());
        dto.setSlotNumber(parkingSlot.getSlotNumber());
        dto.setSlotType(parkingSlot.getSlotType());
        dto.setAvailable(parkingSlot.getAvailable());

        return dto;
    }

    private ParkingSlotResponseDTO mapToResponseDTO(ParkingSlot parkingSlot) {

        ParkingSlotResponseDTO dto = new ParkingSlotResponseDTO();
        dto.setId(parkingSlot.getId());
        dto.setSlotNumber(parkingSlot.getSlotNumber());
        dto.setSlotType(parkingSlot.getSlotType());
        dto.setAvailable(parkingSlot.getAvailable());

        return dto;
    }

    @Transactional
    public void deleteParkingSlotById(int id, String username) {
        ParkingSlot parkingSlot = parkingSlotRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Slot not found!")
        );

        if (!parkingSlot.getParkingLot().getOwner().getUsername().equals(username)) {
            throw new RuntimeException("Authorization Failed!");
        }

        parkingSlotRepository.delete(parkingSlot);
    }
}
