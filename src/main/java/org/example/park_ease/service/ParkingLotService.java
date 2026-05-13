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

        // getting logged in username
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
        parkingLot.setDailyMax(requestDTO.getDailyMax());
        parkingLot.setActive(requestDTO.getActive());

        // backend-controller user
        parkingLot.setOwner(user);

        // saving to Database
        ParkingLot savedParkingLot = parkingLotRepository.save(parkingLot);

        // Entity -> DTO mapping
        ParkingLotResponseDTO dto = new ParkingLotResponseDTO();

        dto.setName(savedParkingLot.getName());
        dto.setLocation(savedParkingLot.getLocation());
        dto.setHourlyRate(savedParkingLot.getHourlyRate());
        dto.setDailyMax(savedParkingLot.getDailyMax());
        dto.setActive(savedParkingLot.getActive());
        dto.setOwnerName(username);

        return dto;

    }

    public List<ParkingLotResponseDTO> getAllParkingLots() {

        List<ParkingLot> parkingLots = parkingLotRepository.findAll();

        return parkingLots.stream()
                .map(parkingLot -> {
                    ParkingLotResponseDTO dto = new ParkingLotResponseDTO();

                    dto.setName(parkingLot.getName());
                    dto.setLocation(parkingLot.getLocation());
                    dto.setHourlyRate(parkingLot.getHourlyRate());
                    dto.setDailyMax(parkingLot.getDailyMax());
                    dto.setActive(parkingLot.getActive());
                    dto.setOwnerName(parkingLot.getOwner().getUsername());

                    return dto;
                })
                .toList();
    }

    public List<ParkingLotResponseDTO> getMyParkingLots(String username) {

        List<ParkingLot> parkingLots = parkingLotRepository.findByOwnerUsername(username);

        return parkingLots.stream()
                .map(parkingLot -> {
                    ParkingLotResponseDTO dto = new ParkingLotResponseDTO();

                    dto.setName(parkingLot.getName());
                    dto.setLocation(parkingLot.getLocation());
                    dto.setHourlyRate(parkingLot.getHourlyRate());
                    dto.setDailyMax(parkingLot.getDailyMax());
                    dto.setActive(parkingLot.getActive());
                    dto.setOwnerName(parkingLot.getOwner().getUsername());

                    return dto;
                })
                .toList();
    }
}
