package com.teachertransfer.controller;

import com.teachertransfer.dto.ApiResponse;
import com.teachertransfer.dto.match.MatchResponse;
import com.teachertransfer.enums.MatchType;
import com.teachertransfer.security.JwtUtil;
import com.teachertransfer.service.MatchingService;
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
    private JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MatchResponse>>> getMatches(
            @RequestParam(defaultValue = "DIRECT") MatchType matchType) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = authentication.getCredentials().toString();
        Long teacherId = jwtUtil.extractTeacherId(token);

        List<MatchResponse> matches = matchingService.getMatches(teacherId, matchType);
        return ResponseEntity.ok(ApiResponse.success("Matches retrieved", matches));
    }

    @GetMapping("/map")
    public ResponseEntity<ApiResponse<List<MatchResponse>>> getMatchesForMap() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = authentication.getCredentials().toString();
        Long teacherId = jwtUtil.extractTeacherId(token);

        List<MatchResponse> matches = matchingService.getMatchesForMap(teacherId);
        return ResponseEntity.ok(ApiResponse.success("Map matches retrieved", matches));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Void>> refreshMatches() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = authentication.getCredentials().toString();
        Long teacherId = jwtUtil.extractTeacherId(token);

        matchingService.refreshMatches(teacherId);
        return ResponseEntity.ok(ApiResponse.success("Matches refreshed"));
    }
}