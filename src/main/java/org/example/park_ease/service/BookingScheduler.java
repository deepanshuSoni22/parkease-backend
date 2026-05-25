package org.example.park_ease.service;

import org.example.park_ease.entity.Booking;
import org.example.park_ease.enums.BookingStatus;
import org.example.park_ease.event.ParkingSlotAvailableEvent;
import org.example.park_ease.repository.BookingRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingScheduler {

    private final BookingRepository bookingRepository;
    private final ApplicationEventPublisher eventPublisher;

    public BookingScheduler(BookingRepository bookingRepository, ApplicationEventPublisher eventPublisher) {
        this.bookingRepository = bookingRepository;
        this.eventPublisher = eventPublisher;
    }

    // Runs every minute to check and auto-complete expired bookings
    // You can adjust the cron expression based on your needs:
    // "0 * * * * *" - every minute
    // "0 */5 * * * *" - every 5 minutes
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void autoCompleteExpiredBookings() {
        LocalDateTime now = LocalDateTime.now();

        // Find all ACTIVE bookings that have expired
        List<Booking> expiredBookings = bookingRepository.findByStatus(BookingStatus.ACTIVE)
                .stream()
                .filter(booking -> isBookingExpired(booking, now))
                .toList();

        for (Booking booking : expiredBookings) {
            completeBookingAutomatically(booking);
        }
    }

    private boolean isBookingExpired(Booking booking, LocalDateTime now) {
        if (booking.getDurationMinutes() == null || booking.getStartTime() == null) {
            return false;
        }

        LocalDateTime expiryTime = booking.getStartTime().plusMinutes(booking.getDurationMinutes());
        return now.isAfter(expiryTime) || now.isEqual(expiryTime);
    }

    private void completeBookingAutomatically(Booking booking) {
        booking.setStatus(BookingStatus.COMPLETED);
        booking.setEndTime(LocalDateTime.now());

        bookingRepository.save(booking);

        // Make slot available again
        var parkingSlot = booking.getParkingSlot();
        parkingSlot.setAvailable(true);

        // Publish event to notify frontend
        eventPublisher.publishEvent(new ParkingSlotAvailableEvent(
                this,
                parkingSlot.getId(),
                parkingSlot.getSlotNumber(),
                parkingSlot.getParkingLot().getId()
        ));
    }


}
