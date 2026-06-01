package org.example.parkease.repository;

import org.example.parkease.entity.Booking;
import org.example.parkease.entity.ParkingSlot;
import org.example.parkease.entity.User;
import org.example.parkease.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    Optional<Booking> findByIdAndUser(Integer id, User user);

    Optional<Booking> findByParkingSlotAndStatus(ParkingSlot parkingSlot, BookingStatus status);

    List<Booking> findByUser(User user);

    List<Booking> findByStatus(BookingStatus status);

    Optional<Booking> findTopByParkingSlotAndStatusInOrderByBookedAtDesc(ParkingSlot parkingSlot, List<BookingStatus> statuses);

    List<Booking> findByStatusIn(List<BookingStatus> statuses);
}
