package org.example.parkease.controller;

import com.razorpay.RazorpayException;
import org.example.parkease.dto.request.VerifyPaymentRequest;
import org.example.parkease.dto.response.CreateOrderResponse;
import org.example.parkease.service.PaymentService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-order/{bookingId}")
    public CreateOrderResponse createOrderResponse(@PathVariable Integer bookingId, Authentication authentication) throws RazorpayException {
        return paymentService.createOrder(bookingId, authentication.getName());
    }

    @PostMapping("/retry/{bookingId}")
    public CreateOrderResponse retryPayment(@PathVariable Integer bookingId, Authentication authentication) throws RazorpayException {
        return paymentService.retryPayment(bookingId, authentication.getName());
    }

    @PostMapping("/verify")
    public String verifyPayment(@RequestBody VerifyPaymentRequest request) {
        paymentService.verifyPayment(request);
        return "Payment Verified";
    }

}
