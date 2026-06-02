package org.example.parkease.repository;

import org.example.parkease.entity.ParkingLot;
import org.example.parkease.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParkingLotRepository extends JpaRepository<ParkingLot, Integer> {

    Optional<ParkingLot> findByName(String name);

    Optional<ParkingLot> findByOwner(User owner);

    List<ParkingLot> findByOwnerUsername(String username);

}
