package org.example.parkease.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.example.parkease.dto.request.VerifyPaymentRequest;
import org.example.parkease.dto.response.CreateOrderResponse;
import org.example.parkease.entity.Booking;
import org.example.parkease.entity.ParkingSlot;
import org.example.parkease.enums.BookingStatus;
import org.example.parkease.repository.BookingRepository;
import org.example.parkease.repository.ParkingSlotRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.parkease.event.ParkingSlotAvailableEvent;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final BookingRepository bookingRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    private final RazorpayClient razorpayClient;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    public PaymentService(BookingRepository bookingRepository, ParkingSlotRepository parkingSlotRepository, RazorpayClient razorpayClient, ApplicationEventPublisher eventPublisher) {
        this.bookingRepository = bookingRepository;
        this.parkingSlotRepository = parkingSlotRepository;
        this.razorpayClient = razorpayClient;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public CreateOrderResponse createOrder(Integer bookingId, String username) throws RazorpayException {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized");
        }

        if (booking.getStatus() != BookingStatus.PENDING_PAYMENT) {
            throw new RuntimeException("Booking already processed");
        }

        long amountInPaise = booking.getAmount()
                        .multiply(BigDecimal.valueOf(100))
                        .longValue();

        // CREATE ORDER
        JSONObject options = new JSONObject();

        options.put("amount", amountInPaise);
        options.put("currency", "INR");
        options.put("receipt", booking.getId().toString());

        Order order = razorpayClient.orders.create(options);

        // SAVE ORDER ID
        String orderId = order.get("id");

        booking.setRazorpayOrderId(orderId);

        bookingRepository.save(booking);

        CreateOrderResponse response = new CreateOrderResponse();

        response.setBookingId(booking.getId());
        response.setOrderId(orderId);
        response.setAmount(amountInPaise);
        response.setKey(keyId);

        return response;

    }

    @Transactional
    public CreateOrderResponse retryPayment(Integer bookingId, String username) throws RazorpayException {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized");
        }

        // Only allow retry for failed or expired payments
        if (booking.getStatus() != BookingStatus.PAYMENT_FAILED && booking.getStatus() != BookingStatus.PAYMENT_EXPIRED) {
            throw new RuntimeException("Booking not eligible for retry");
        }

        ParkingSlot parkingSlot = booking.getParkingSlot();
        if (parkingSlot == null) {
            throw new RuntimeException("Booking has no associated parking slot");
        }

        // If the slot is no longer available to re-reserve, fail the retry
        if (!parkingSlot.getAvailable()) {
            throw new RuntimeException("Parking slot is no longer available");
        }

        // Reserve the slot again
        parkingSlot.setAvailable(false);
        parkingSlotRepository.save(parkingSlot);

        // Reset payment metadata
        booking.setRazorpayOrderId(null);
        booking.setRazorpayPaymentId(null);
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        booking.setRetryAttempts((booking.getRetryAttempts() == null ? 0 : booking.getRetryAttempts()) + 1);

        bookingRepository.save(booking);

        long amountInPaise = booking.getAmount()
                .multiply(BigDecimal.valueOf(100))
                .longValue();

        // CREATE NEW RAZORPAY ORDER
        JSONObject options = new JSONObject();

        options.put("amount", amountInPaise);
        options.put("currency", "INR");
        options.put("receipt", booking.getId().toString());

        Order order = razorpayClient.orders.create(options);

        // SAVE ORDER ID
        String orderId = order.get("id");

        booking.setRazorpayOrderId(orderId);

        bookingRepository.save(booking);

        CreateOrderResponse response = new CreateOrderResponse();

        response.setBookingId(booking.getId());
        response.setOrderId(orderId);
        response.setAmount(amountInPaise);
        response.setKey(keyId);

        return response;

    }

    @Transactional
    public void verifyPayment(VerifyPaymentRequest request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found!"));

        // Signature verification
        boolean valid = verifySignature(
                request.getRazorpayOrderId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySignature()
        );

        if (!valid) {
            booking.setStatus(BookingStatus.PAYMENT_FAILED);

            // Release the reserved slot when payment verification fails
            ParkingSlot parkingSlot = booking.getParkingSlot();
            if (parkingSlot != null) {
                parkingSlot.setAvailable(true);
                parkingSlotRepository.save(parkingSlot);

                // Publish availability event
                eventPublisher.publishEvent(new ParkingSlotAvailableEvent(
                        parkingSlot.getId(),
                        parkingSlot.getSlotNumber(),
                        parkingSlot.getParkingLot().getId()
                ));
            }

            bookingRepository.save(booking);

            throw new RuntimeException("Invalid Razorpay Signature");
        }

        if (!booking.getRazorpayOrderId()
                .equals(request.getRazorpayOrderId())) {

            // Treat order mismatch as a failed payment and release slot
            booking.setStatus(BookingStatus.PAYMENT_FAILED);
            ParkingSlot parkingSlot = booking.getParkingSlot();
            if (parkingSlot != null) {
                parkingSlot.setAvailable(true);
                parkingSlotRepository.save(parkingSlot);
                eventPublisher.publishEvent(new ParkingSlotAvailableEvent(
                        parkingSlot.getId(),
                        parkingSlot.getSlotNumber(),
                        parkingSlot.getParkingLot().getId()
                ));
            }
            bookingRepository.save(booking);

            throw new RuntimeException("Order mismatch");
        }

        booking.setStatus(BookingStatus.CONFIRMED);

        booking.setPaidAt(LocalDateTime.now());

        booking.setRazorpayPaymentId(request.getRazorpayPaymentId());

        booking.setStartTime(LocalDateTime.now());

        ParkingSlot parkingSlot = booking.getParkingSlot();

        parkingSlot.setAvailable(false);

        parkingSlotRepository.save(parkingSlot);
        bookingRepository.save(booking);

    }

    private boolean verifySignature(String orderId, String paymentId, String razorpaySignature) {

        try {

            String payload = orderId + "|" + paymentId;

            Mac sha256Hmac = Mac.getInstance("HmacSHA256");

            SecretKeySpec secretKey = new SecretKeySpec(
                    keySecret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );

            sha256Hmac.init(secretKey);

            byte[] hash = sha256Hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8));

            StringBuilder generatedSignature = new StringBuilder();

            for (byte b : hash) {
                generatedSignature.append(String.format("%02x", b));
            }

            return generatedSignature
                    .toString()
                    .equals(razorpaySignature);

        } catch (Exception e) {
            throw new RuntimeException("Signature verification failed", e);
        }
    }
}
