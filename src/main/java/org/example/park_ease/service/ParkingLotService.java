package org.example.park_ease.service;

import org.example.park_ease.dto.request.ParkingLotRequestDTO;
import org.example.park_ease.dto.response.ParkingLotResponseDTO;
import org.example.park_ease.entity.ParkingLot;
import org.example.park_ease.entity.User;
import org.example.park_ease.repository.ParkingLotRepository;
import org.example.park_ease.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ParkingLotService {

    private final ParkingLotRepository parkingLotRepository;
    private final UserRepository userRepository;

    public ParkingLotService(ParkingLotRepository parkingLotRepository, UserRepository userRepository) {
        this.parkingLotRepository = parkingLotRepository;
        this.userRepository = userRepository;
    }

    public ParkingLotResponseDTO getParkingLotById(int id) {

        ParkingLot parkingLot =  parkingLotRepository.findById(id).orElseThrow(
                () -> new RuntimeException("ParkingLot not found!")
        );

        return mapToResponseDTO(parkingLot);
    }

    public ParkingLotResponseDTO createParkingLot(ParkingLotRequestDTO requestDTO, String username) {

        if (parkingLotRepository.findByName(requestDTO.getName()).isPresent()) {
            throw new RuntimeException("Parking Lot Already Exists!");
        }

        User user = userRepository
                .findByUsername(username)
                .orElseThrow();


        // DTO -> Entity mapping
        ParkingLot parkingLot = new ParkingLot();

        parkingLot.setName(requestDTO.getName());
        parkingLot.setLocation(requestDTO.getLocation());
        parkingLot.setActive(requestDTO.getActive());

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

    @Transactional
    public void deleteParkingLot(int id, String username) {

        ParkingLot parkingLot = parkingLotRepository.findById(id).orElseThrow(
                () -> new RuntimeException("ParkingLot not found!")
        );

        if (!parkingLot.getOwner().getUsername().equals(username)) {
            throw new RuntimeException("Authorization Failed!");
        }

        User owner = parkingLot.getOwner();
        owner.setParkingLot(null);

        userRepository.save(owner);

        parkingLotRepository.delete(parkingLot);
    }

    // Private mapper method
    private ParkingLotResponseDTO mapToResponseDTO(ParkingLot parkingLot) {

        ParkingLotResponseDTO dto = new ParkingLotResponseDTO();
        dto.setId(parkingLot.getId());
        dto.setName(parkingLot.getName());
        dto.setLocation(parkingLot.getLocation());
        dto.setActive(parkingLot.getActive());
        dto.setOwnerName(parkingLot.getOwner().getUsername());
        return dto;

    }

}
