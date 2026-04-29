package com.teachertransfer.service;

import com.teachertransfer.dto.payment.CreateOrderRequest;
import com.teachertransfer.dto.payment.VerifyPaymentRequest;
import com.teachertransfer.entity.Payment;
import com.teachertransfer.entity.SubscriptionPlan;
import com.teachertransfer.entity.Teacher;
import com.teachertransfer.enums.PaymentStatus;
import com.teachertransfer.enums.SubscriptionStatus;
import com.teachertransfer.repository.PaymentRepository;
import com.teachertransfer.repository.SubscriptionPlanRepository;
import com.teachertransfer.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Value("${app.razorpay.key-id}")
    private String razorpayKeyId;

    @Value("${app.razorpay.key-secret}")
    private String razorpayKeySecret;

    public Map<String, Object> createOrder(CreateOrderRequest request, Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        SubscriptionPlan plan = subscriptionPlanRepository.findByCode(request.getPlanCode())
                .orElseThrow(() -> new RuntimeException("Invalid plan code"));

        // TODO: Integrate with Razorpay to create order
        // For now, return mock response
        String orderId = "order_" + System.currentTimeMillis();

        // Create payment record
        Payment payment = new Payment();
        payment.setTeacherId(teacherId);
        payment.setRazorpayOrderId(orderId);
        payment.setAmountPaise(plan.getPricePaise());
        payment.setCurrency("INR");
        payment.setPlan(request.getPlanCode());
        payment.setStatus(PaymentStatus.PENDING.getCode());
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepository.save(payment);

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderId);
        response.put("amount", plan.getPricePaise());
        response.put("currency", "INR");
        response.put("razorpayKey", razorpayKeyId);

        return response;
    }

    public Map<String, Object> verifyPayment(VerifyPaymentRequest request, Long teacherId) {
        // Find payment record
        Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // Verify payment belongs to teacher
        if (!payment.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("Payment does not belong to this teacher");
        }

        // TODO: Verify Razorpay signature
        // For now, mark as successful

        // Update payment record
        payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
        payment.setRazorpaySignature(request.getRazorpaySignature());
        payment.setStatus(PaymentStatus.SUCCESS.getCode());
        payment.setCompletedAt(LocalDateTime.now());

        paymentRepository.save(payment);

        // Update teacher subscription
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        SubscriptionPlan plan = subscriptionPlanRepository.findByCode(payment.getPlan())
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        teacher.setSubscriptionStatus(SubscriptionStatus.PAID_ACTIVE.getCode());
        teacher.setSubscriptionPlan(payment.getPlan());
        teacher.setSubscriptionExpiresAt(LocalDateTime.now().plusDays(plan.getDurationDays()));
        teacherRepository.save(teacher);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("subscriptionStatus", SubscriptionStatus.PAID_ACTIVE.getCode());
        response.put("subscriptionPlan", payment.getPlan());
        response.put("subscriptionExpiresAt", teacher.getSubscriptionExpiresAt());

        return response;
    }

    public SubscriptionPlan getPlanByCode(String planCode) {
        return subscriptionPlanRepository.findByCode(planCode)
                .orElseThrow(() -> new RuntimeException("Plan not found"));
    }

    public java.util.List<SubscriptionPlan> getAllActivePlans() {
        return subscriptionPlanRepository.findByIsActiveTrueOrderByPricePaiseAsc();
    }
}