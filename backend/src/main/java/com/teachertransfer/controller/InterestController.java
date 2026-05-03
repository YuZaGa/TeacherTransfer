package com.teachertransfer.controller;

import com.teachertransfer.dto.ApiResponse;
import com.teachertransfer.dto.interest.InterestResponse;
import com.teachertransfer.security.JwtUtil;
import com.teachertransfer.service.InterestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/interest")
@CrossOrigin(origins = "*")
public class InterestController {

    @Autowired
    private InterestService interestService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/{teacherId}")
    public ResponseEntity<ApiResponse<InterestResponse>> sendInterest(@PathVariable Long teacherId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = authentication.getCredentials().toString();
        Long fromTeacherId = jwtUtil.extractTeacherId(token);

        InterestResponse response = interestService.sendInterest(fromTeacherId, teacherId);
        return ResponseEntity.ok(ApiResponse.success("Interest sent", response));
    }

    @PostMapping("/{interestId}/accept")
    public ResponseEntity<ApiResponse<InterestResponse>> acceptInterest(@PathVariable Long interestId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = authentication.getCredentials().toString();
        Long teacherId = jwtUtil.extractTeacherId(token);

        InterestResponse response = interestService.acceptInterest(interestId, teacherId);
        return ResponseEntity.ok(ApiResponse.success("Interest accepted", response));
    }

    @PostMapping("/{interestId}/reject")
    public ResponseEntity<ApiResponse<InterestResponse>> rejectInterest(@PathVariable Long interestId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = authentication.getCredentials().toString();
        Long teacherId = jwtUtil.extractTeacherId(token);

        InterestResponse response = interestService.rejectInterest(interestId, teacherId);
        return ResponseEntity.ok(ApiResponse.success("Interest rejected", response));
    }

    @DeleteMapping("/{interestId}")
    public ResponseEntity<ApiResponse<Void>> withdrawInterest(@PathVariable Long interestId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = authentication.getCredentials().toString();
        Long teacherId = jwtUtil.extractTeacherId(token);

        interestService.withdrawInterest(interestId, teacherId);
        return ResponseEntity.ok(ApiResponse.success("Interest withdrawn", null));
    }

    @GetMapping("/sent")
    public ResponseEntity<ApiResponse<List<InterestResponse>>> getSentInterests() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = authentication.getCredentials().toString();
        Long teacherId = jwtUtil.extractTeacherId(token);

        List<InterestResponse> interests = interestService.getSentInterests(teacherId);
        return ResponseEntity.ok(ApiResponse.success("Sent interests retrieved", interests));
    }

    @GetMapping("/received")
    public ResponseEntity<ApiResponse<List<InterestResponse>>> getReceivedInterests() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = authentication.getCredentials().toString();
        Long teacherId = jwtUtil.extractTeacherId(token);

        List<InterestResponse> interests = interestService.getReceivedInterests(teacherId);
        return ResponseEntity.ok(ApiResponse.success("Received interests retrieved", interests));
    }
}