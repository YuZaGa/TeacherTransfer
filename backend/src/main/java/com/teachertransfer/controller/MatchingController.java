package com.teachertransfer.controller;

import com.teachertransfer.dto.ApiResponse;
import com.teachertransfer.dto.match.MatchResponse;
import com.teachertransfer.enums.MatchType;
import com.teachertransfer.security.JwtUtil;
import com.teachertransfer.service.MatchingService;
import com.teachertransfer.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matches")
@CrossOrigin(origins = "*")
public class MatchingController {

    @Autowired
    private MatchingService matchingService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MatchResponse>>> getDiscoveryMatches() {
        Long teacherId = extractTeacherId();
        teacherService.touchInteraction(teacherId);
        List<MatchResponse> matches = matchingService.getDiscoveryMatches(teacherId);
        return ResponseEntity.ok(ApiResponse.success("Discovery matches retrieved", matches));
    }

    @GetMapping("/mutual")
    public ResponseEntity<ApiResponse<List<MatchResponse>>> getMutualMatches() {
        Long teacherId = extractTeacherId();
        teacherService.touchInteraction(teacherId);
        List<MatchResponse> matches = matchingService.getMutualMatches(teacherId);
        return ResponseEntity.ok(ApiResponse.success("Mutual matches retrieved", matches));
    }

    @GetMapping("/interest-sent")
    public ResponseEntity<ApiResponse<List<MatchResponse>>> getInterestSentMatches() {
        Long teacherId = extractTeacherId();
        teacherService.touchInteraction(teacherId);
        List<MatchResponse> matches = matchingService.getInterestSentMatches(teacherId);
        return ResponseEntity.ok(ApiResponse.success("Interest sent matches retrieved", matches));
    }

    @GetMapping("/map")
    public ResponseEntity<ApiResponse<List<MatchResponse>>> getMatchesForMap() {
        Long teacherId = extractTeacherId();
        List<MatchResponse> matches = matchingService.getMatchesForMap(teacherId);
        return ResponseEntity.ok(ApiResponse.success("Map matches retrieved", matches));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Void>> refreshMatches() {
        Long teacherId = extractTeacherId();
        matchingService.refreshMatches(teacherId);
        return ResponseEntity.ok(ApiResponse.success("Matches refreshed"));
    }

    private Long extractTeacherId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = authentication.getCredentials().toString();
        return jwtUtil.extractTeacherId(token);
    }
}
