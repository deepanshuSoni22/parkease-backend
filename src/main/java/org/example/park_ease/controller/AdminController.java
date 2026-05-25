package org.example.park_ease.controller;

import org.example.park_ease.dto.response.ParkingLotResponseDTO;
import org.example.park_ease.dto.response.UserResponseDTO;
import org.example.park_ease.dto.response.BookingResponseDTO;
import org.example.park_ease.service.ParkingLotService;
import org.example.park_ease.service.UserService;
import org.example.park_ease.service.BookingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final UserService userService;
    private final ParkingLotService parkingLotService;
    private final BookingService bookingService;

    public AdminController(UserService userService, ParkingLotService parkingLotService, BookingService bookingService) {
        this.userService = userService;
        this.parkingLotService = parkingLotService;
        this.bookingService = bookingService;
    }

    @GetMapping("/users")
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{userId}")
    public UserResponseDTO getUser(@PathVariable int userId) {
        return userService.getUser(userId);
    }

    @GetMapping("/parking-lots")
    public List<ParkingLotResponseDTO> listAllParkingLots() {
        return parkingLotService.getAllParkingLots();
    }



    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable int userId) {
        userService.deleteUser(userId);
    }

    @GetMapping("/bookings")
    @PreAuthorize("hasRole('ADMIN')")
    public List<BookingResponseDTO> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/bookings/{bookingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public BookingResponseDTO getBooking(@PathVariable Integer bookingId) {
        return bookingService.getBookingById(bookingId);
    }

    @PutMapping("/bookings/{bookingId}/complete")
    @PreAuthorize("hasRole('ADMIN')")
    public BookingResponseDTO completeBooking(@PathVariable Integer bookingId) {
        return bookingService.completeBookingAsAdmin(bookingId);
    }

}
