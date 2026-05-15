package org.example.park_ease.controller;

import org.example.park_ease.dto.request.BookingRequestDTO;
import org.example.park_ease.dto.response.BookingResponseDTO;
import org.example.park_ease.service.BookingService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingResponseDTO createBooking(@RequestBody BookingRequestDTO requestDTO, Authentication authentication) {
        return bookingService.createBooking(requestDTO, authentication.getName());
    }

    @PutMapping("/{bookingId}/complete")
    public BookingResponseDTO completeBooking(@PathVariable Integer bookingId, Authentication authentication) {
        return bookingService.completeBooking(bookingId, authentication.getName());
    }

    @GetMapping("/my")
    public List<BookingResponseDTO> getMyBookings(Authentication authentication) {
        return bookingService.getUserBookings(authentication.getName());
    }

}
