package org.example.park_ease.repository;

import org.example.park_ease.entity.ParkingSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Integer> {

    List<ParkingSlot> findByParkingLot_Id(int parkingLotId);
}
