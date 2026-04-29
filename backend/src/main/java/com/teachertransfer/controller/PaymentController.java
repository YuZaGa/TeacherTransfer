package com.teachertransfer.controller;

import com.teachertransfer.dto.ApiResponse;
import com.teachertransfer.dto.payment.CreateOrderRequest;
import com.teachertransfer.dto.payment.VerifyPaymentRequest;
import com.teachertransfer.entity.SubscriptionPlan;
import com.teachertransfer.security.JwtUtil;
import com.teachertransfer.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/create-order")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = authentication.getCredentials().toString();
        Long teacherId = jwtUtil.extractTeacherId(token);

        Map<String, Object> response = paymentService.createOrder(request, teacherId);
        return ResponseEntity.ok(ApiResponse.success("Order created", response));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyPayment(@Valid @RequestBody VerifyPaymentRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = authentication.getCredentials().toString();
        Long teacherId = jwtUtil.extractTeacherId(token);

        Map<String, Object> response = paymentService.verifyPayment(request, teacherId);
        return ResponseEntity.ok(ApiResponse.success("Payment verified", response));
    }

    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<List<SubscriptionPlan>>> getAllPlans() {
        List<SubscriptionPlan> plans = paymentService.getAllActivePlans();
        return ResponseEntity.ok(ApiResponse.success("Plans retrieved", plans));
    }

    @GetMapping("/plans/{planCode}")
    public ResponseEntity<ApiResponse<SubscriptionPlan>> getPlanByCode(@PathVariable String planCode) {
        SubscriptionPlan plan = paymentService.getPlanByCode(planCode);
        return ResponseEntity.ok(ApiResponse.success("Plan retrieved", plan));
    }
}