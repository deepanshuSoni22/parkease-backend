package org.example.park_ease.repository;

import org.example.park_ease.entity.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParkingLotRepository extends JpaRepository<ParkingLot, Integer> {

    Optional<ParkingLot> findByName(String name);

    List<ParkingLot> findByOwnerUsername(String username);

}
