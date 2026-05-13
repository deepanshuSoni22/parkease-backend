package org.example.park_ease.repository;

import org.example.park_ease.entity.ParkingSlot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Integer> {
}
