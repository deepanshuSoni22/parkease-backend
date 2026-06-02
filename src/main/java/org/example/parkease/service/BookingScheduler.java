package org.example.parkease.service;

import org.example.parkease.entity.Booking;
import org.example.parkease.enums.BookingStatus;
import org.example.parkease.repository.BookingRepository;
import org.example.parkease.event.ParkingSlotAvailableEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingScheduler {

    private static final Logger log = LoggerFactory.getLogger(BookingScheduler.class);
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final org.example.parkease.repository.ParkingSlotRepository parkingSlotRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${parkease.booking.pending-payment-timeout-minutes:15}")
    private int pendingPaymentTimeoutMinutes;


    public BookingScheduler(BookingRepository bookingRepository, BookingService bookingService, ApplicationEventPublisher eventPublisher, org.example.parkease.repository.ParkingSlotRepository parkingSlotRepository) {
        this.bookingRepository = bookingRepository;
        this.bookingService = bookingService;
        this.eventPublisher = eventPublisher;
        this.parkingSlotRepository = parkingSlotRepository;
    }

    // Runs every minute to check and auto-complete expired bookings
    // You can adjust the cron expression based on your needs:
    // "0 * * * * *" - every minute
    // "0 */5 * * * *" - every 5 minutes
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void autoCompleteExpiredBookings() {
        LocalDateTime now = LocalDateTime.now();

        List<BookingStatus> statuses = List.of(BookingStatus.ACTIVE, BookingStatus.CONFIRMED);
        List<Booking> expiredBookings = bookingRepository.findByStatusIn(statuses).stream()
                .filter(booking -> isBookingExpired(booking, now))
                .toList();


        log.info("BookingScheduler: found {} expired bookings", expiredBookings.size());
        for (Booking booking : expiredBookings) {
            try {
                bookingService.completeBookingInternal(booking);
            } catch (Exception e) {
                log.error("Failed auto-completing booking id={}", booking.getId(), e);
            }
        }
    }

    // Runs every 30 seconds to clean up stale PENDING_PAYMENT bookings
    // Reduced interval to minimize slot hold latency while keeping load low for small-scale deployments
    @Scheduled(cron = "*/30 * * * * *")
    @Transactional
    public void cleanupStalePendingPayments() {
        LocalDateTime now = LocalDateTime.now();

        long start = System.currentTimeMillis();

        List<Booking> pending = bookingRepository.findByStatus(BookingStatus.PENDING_PAYMENT);

        List<Booking> stale = pending.stream()
                .filter(b -> b.getBookedAt() != null && b.getBookedAt().plusMinutes(pendingPaymentTimeoutMinutes).isBefore(now))
                .toList();

        log.info("BookingScheduler: found {} stale PENDING_PAYMENT bookings", stale.size());

        for (Booking booking : stale) {
            try {
                booking.setStatus(BookingStatus.PAYMENT_EXPIRED);

                // Release slot
                if (booking.getParkingSlot() != null) {
                    var slot = booking.getParkingSlot();
                    slot.setAvailable(true);
                    parkingSlotRepository.save(slot);
                }

                bookingRepository.save(booking);

                if (booking.getParkingSlot() != null) {
                    eventPublisher.publishEvent(new ParkingSlotAvailableEvent(
                            booking.getParkingSlot().getId(),
                            booking.getParkingSlot().getSlotNumber(),
                            booking.getParkingSlot().getParkingLot().getId()
                    ));
                }

            } catch (Exception e) {
                log.error("Failed to cleanup stale PENDING_PAYMENT booking id={}", booking.getId(), e);
            }
        }

        long durationMs = System.currentTimeMillis() - start;
        log.info("BookingScheduler: cleanupStalePendingPayments completed in {} ms, processed {} bookings", durationMs, stale.size());
    }

    private boolean isBookingExpired(Booking booking, LocalDateTime now) {
        Integer duration = booking.getDurationMinutes();
        LocalDateTime start = booking.getStartTime() != null ? booking.getStartTime() : booking.getBookedAt();
        if (duration == null || start == null) return false;
        LocalDateTime expiryTime = start.plusMinutes(duration);
        return !now.isBefore(expiryTime); // isAfter or equal => expired
    }

}
