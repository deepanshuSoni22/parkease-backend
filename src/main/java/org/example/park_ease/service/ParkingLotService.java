package org.example.park_ease.service;

import org.example.park_ease.dto.request.ParkingLotRequestDTO;
import org.example.park_ease.dto.response.ParkingLotResponseDTO;
import org.example.park_ease.entity.ParkingLot;
import org.example.park_ease.entity.User;
import org.example.park_ease.repository.ParkingLotRepository;
import org.example.park_ease.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingLotService {

    private final ParkingLotRepository parkingLotRepository;
    private final UserRepository userRepository;

    public ParkingLotService(ParkingLotRepository parkingLotRepository, UserRepository userRepository) {
        this.parkingLotRepository = parkingLotRepository;
        this.userRepository = userRepository;
    }

    public ParkingLotResponseDTO createParkingLot(ParkingLotRequestDTO requestDTO) {

        if (parkingLotRepository.findByName(requestDTO.getName()).isPresent()) {
            throw new RuntimeException("Parking Lot Already Exists!");
        }

        // get logged-in username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository
                .findByUsername(username)
                .orElseThrow();


        // DTO -> Entity mapping
        ParkingLot parkingLot = new ParkingLot();

        parkingLot.setName(requestDTO.getName());
        parkingLot.setLocation(requestDTO.getLocation());
        parkingLot.setHourlyRate(requestDTO.getHourlyRate());
        parkingLot.setTotalSlots(requestDTO.getTotalSlots());
        parkingLot.setIsActive(requestDTO.getIsActive());

        // backend-controller user
        parkingLot.setOwner(user);

        // saving to Database
        ParkingLot savedParkingLot = parkingLotRepository.save(parkingLot);

        return mapToResponseDTO(savedParkingLot);

    }

    public List<ParkingLotResponseDTO> getAllParkingLots() {

        return parkingLotRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();

    }

    public List<ParkingLotResponseDTO> getMyParkingLots(String username) {

        return parkingLotRepository.findByOwnerUsername(username)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();

    }

    // Private mapper method
    private ParkingLotResponseDTO mapToResponseDTO(ParkingLot parkingLot) {

        ParkingLotResponseDTO dto = new ParkingLotResponseDTO();
        dto.setName(parkingLot.getName());
        dto.setLocation(parkingLot.getLocation());
        dto.setHourlyRate(parkingLot.getHourlyRate());
        dto.setTotalSlots(parkingLot.getTotalSlots());
        dto.setIsActive(parkingLot.getIsActive());
        dto.setOwnerName(parkingLot.getOwner().getUsername());
        return dto;

    }

}
