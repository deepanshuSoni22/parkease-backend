package org.example.park_ease.repository;

import org.example.park_ease.entity.Booking;
import org.example.park_ease.entity.ParkingSlot;
import org.example.park_ease.entity.User;
import org.example.park_ease.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    Optional<Booking> findByIdAndUser(Integer id, User user);

    Optional<Booking> findByParkingSlotAndStatus(ParkingSlot parkingSlot, BookingStatus status);

    List<Booking> findByUser(User user);

    List<Booking> findByStatus(BookingStatus status);
}
