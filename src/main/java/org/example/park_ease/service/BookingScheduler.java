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
    private final BookingService bookingService;

    public BookingScheduler(BookingRepository bookingRepository, BookingService bookingService) {
        this.bookingRepository = bookingRepository;
        this.bookingService = bookingService;
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
            if (booking.getStatus() != BookingStatus.COMPLETED) {
                bookingService.completeBookingInternal(booking);
            }
        }
    }

    private boolean isBookingExpired(Booking booking, LocalDateTime now) {
        if (booking.getDurationMinutes() == null || booking.getStartTime() == null) {
            return false;
        }

        LocalDateTime expiryTime = booking.getStartTime().plusMinutes(booking.getDurationMinutes());
        return now.isAfter(expiryTime) || now.isEqual(expiryTime);
    }

}
