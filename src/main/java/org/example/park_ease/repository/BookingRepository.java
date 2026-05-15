package org.example.park_ease.repository;

import org.example.park_ease.entity.Booking;
import org.example.park_ease.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    Optional<Booking> findByIdAndUser(Integer id, User user);

    List<Booking> findByUser(User user);
}
