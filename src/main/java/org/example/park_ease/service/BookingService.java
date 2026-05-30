package org.example.park_ease.service;

import org.example.park_ease.dto.request.BookingRequestDTO;
import org.example.park_ease.dto.response.BookingResponseDTO;
import org.example.park_ease.entity.Booking;
import org.example.park_ease.entity.ParkingSlot;
import org.example.park_ease.entity.User;
import org.example.park_ease.enums.BookingStatus;
import org.example.park_ease.event.ParkingSlotAvailableEvent;
import org.example.park_ease.exception.SlotNotAvailableException;
import org.example.park_ease.repository.BookingRepository;
import org.example.park_ease.repository.ParkingSlotRepository;
import org.example.park_ease.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public BookingService(BookingRepository bookingRepository, ParkingSlotRepository parkingSlotRepository, UserRepository userRepository, ApplicationEventPublisher eventPublisher) {
        this.bookingRepository = bookingRepository;
        this.parkingSlotRepository = parkingSlotRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO requestDTO, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // Get parking slot
        ParkingSlot parkingSlot = parkingSlotRepository.findById(requestDTO.getSlotId())
                .orElseThrow(() -> new RuntimeException("Parking slot not found!"));

        // Check if slot is available
        if (parkingSlot.getAvailable() != true) {
            throw new SlotNotAvailableException("Parking slot is already booked!");
        }

        // Validate duration
        Integer duration = requestDTO.getDurationMinutes();
        if (duration == null || duration <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0 minutes!");
        }

        // Create Booking
        Booking booking = new Booking();

        booking.setUser(user);
        booking.setParkingSlot(parkingSlot);
        booking.setStatus(BookingStatus.ACTIVE);
        booking.setBookedAt(LocalDateTime.now());
        booking.setStartTime(LocalDateTime.now());
        booking.setDurationMinutes(duration);

//        // amount calculation
//        BigDecimal amount = parkingSlot.getPricePerMinute().multiply(
//                BigDecimal.valueOf(requestDTO.getDurationMinutes())
//        );
//        // set amount
//        booking.setAmount(amount);

        // Update Parking available to false, has been booked
        parkingSlot.setAvailable(false);
        parkingSlotRepository.save(parkingSlot);

        // Save Booking
        bookingRepository.save(booking);

        // Map to DTO
        return mapToResponseDTO(booking);
    }

    /**     * Internal method for both auto and manual completion     */
    @Transactional
    protected BookingResponseDTO completeBookingInternal(Booking booking) {

        // Update booking status
        booking.setStatus(BookingStatus.COMPLETED);
        booking.setEndTime(LocalDateTime.now());

        // Get parking slot and update its status
        ParkingSlot parkingSlot = booking.getParkingSlot();
        parkingSlot.setAvailable(true);
        parkingSlotRepository.save(parkingSlot);

        // Save booking
        bookingRepository.save(booking);

        // Publish event to notify frontend
        eventPublisher.publishEvent(new ParkingSlotAvailableEvent(
                parkingSlot.getId(),
                parkingSlot.getSlotNumber(),
                parkingSlot.getParkingLot().getId()
        ));

        // Map to DTO
        return mapToResponseDTO(booking);
    }

    // FOR ADMIN and USER WHO MADE BOOKING
    @Transactional
    public BookingResponseDTO completeBooking(Integer bookingId, String username) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found!"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        boolean isAdmin = user.getRole() != null && "ADMIN".equalsIgnoreCase(user.getRole().name());
        boolean isOwner = booking.getUser() != null && booking.getUser().getId().equals(user.getId());

        if (!isAdmin && !isOwner) {
            throw new RuntimeException("You are not allowed to complete this booking!");
        }

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalStateException("Booking is already completed!");
        }

        return completeBookingInternal(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getUserBookings(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // Get user bookings
        List<Booking> bookings = bookingRepository.findByUser(user);

        // Map to DTOs
        return bookings.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getAllBookings(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        boolean isAdmin = user.getRole() != null && "ADMIN".equalsIgnoreCase(user.getRole().name());

        if (!isAdmin) {
            throw new RuntimeException("You are not allowed to view all bookings!");
        }

        List<Booking> bookings = bookingRepository.findAll();
        return bookings.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public BookingResponseDTO getBookingById(Integer bookingId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        boolean isAdmin = user.getRole() != null && "ADMIN".equalsIgnoreCase(user.getRole().name());

        if (!isAdmin) {
            throw new RuntimeException("You are not allowed to view this booking!");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found!"));

        return mapToResponseDTO(booking);
    }

    private BookingResponseDTO mapToResponseDTO(Booking booking) {
        BookingResponseDTO dto = new BookingResponseDTO();

        dto.setId(booking.getId());
        dto.setSlotId(booking.getParkingSlot().getId());
        dto.setSlotNumber(booking.getParkingSlot().getSlotNumber());
        dto.setSlotType(booking.getParkingSlot().getSlotType());
        dto.setParkingLotId(booking.getParkingSlot().getParkingLot().getId());
        dto.setParkingLotName(booking.getParkingSlot().getParkingLot().getName());
        dto.setStatus(booking.getStatus());
        dto.setBookedAt(booking.getBookedAt());
        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());
        dto.setDurationMinutes(booking.getDurationMinutes());
        dto.setBookedByUsername(booking.getUser().getUsername());
//        dto.setAmount(booking.getAmount());

        return dto;
    }



}
