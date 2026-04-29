package com.teachertransfer.controller;

import com.teachertransfer.dto.ApiResponse;
import com.teachertransfer.dto.teacher.TeacherResponse;
import com.teachertransfer.security.JwtUtil;
import com.teachertransfer.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teacher")
@CrossOrigin(origins = "*")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<TeacherResponse>> getCurrentTeacher() {
        TeacherResponse response = teacherService.getCurrentTeacherProfile();
        return ResponseEntity.ok(ApiResponse.success("Teacher profile retrieved", response));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<TeacherResponse>> updateCurrentTeacher(@Valid @RequestBody TeacherResponse updateRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = authentication.getCredentials().toString();
        Long teacherId = jwtUtil.extractTeacherId(token);

        TeacherResponse response = teacherService.updateTeacherProfile(teacherId, updateRequest);
        return ResponseEntity.ok(ApiResponse.success("Teacher profile updated", response));
    }

    @GetMapping("/{teacherId}")
    public ResponseEntity<ApiResponse<TeacherResponse>> getTeacherById(@PathVariable Long teacherId) {
        TeacherResponse response = teacherService.getTeacherProfile(teacherId);
        return ResponseEntity.ok(ApiResponse.success("Teacher profile retrieved", response));
    }
}