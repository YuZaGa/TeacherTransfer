package com.teachertransfer.service;

import com.teachertransfer.dto.teacher.TeacherResponse;
import com.teachertransfer.entity.Teacher;
import com.teachertransfer.entity.TeacherGeoIndex;
import com.teachertransfer.enums.*;
import com.teachertransfer.repository.TeacherGeoIndexRepository;
import com.teachertransfer.repository.TeacherRepository;
import com.teachertransfer.util.GeohashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private TeacherGeoIndexRepository teacherGeoIndexRepository;

    public Teacher getCurrentTeacher() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phone = authentication.getName();
        return teacherRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
    }

    public TeacherResponse getTeacherProfile(Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        return mapToResponse(teacher);
    }

    public TeacherResponse getCurrentTeacherProfile() {
        Teacher teacher = getCurrentTeacher();
        return mapToResponse(teacher);
    }

    public TeacherResponse updateTeacherProfile(Long teacherId, TeacherResponse updateRequest) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        // Update fields
        if (updateRequest.getName() != null) {
            teacher.setName(updateRequest.getName());
        }
        if (updateRequest.getEmail() != null) {
            teacher.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getGender() != null) {
            teacher.setGender(updateRequest.getGender().getCode());
        }
        if (updateRequest.getEmployeeId() != null) {
            teacher.setEmployeeId(updateRequest.getEmployeeId());
        }
        if (updateRequest.getUdiseCode() != null) {
            teacher.setUdiseCode(updateRequest.getUdiseCode());
        }
        if (updateRequest.getSchoolName() != null) {
            teacher.setSchoolName(updateRequest.getSchoolName());
        }
        if (updateRequest.getSubject() != null) {
            teacher.setSubject(updateRequest.getSubject().getCode());
        }
        if (updateRequest.getSchoolType() != null) {
            teacher.setSchoolType(updateRequest.getSchoolType().getCode());
        }
        if (updateRequest.getCurrentLocation() != null) {
            teacher.setCurrentDistrictId(updateRequest.getCurrentLocation().getDistrictId());
            teacher.setCurrentBlockId(updateRequest.getCurrentLocation().getBlockId());
            teacher.setCurrentLat(updateRequest.getCurrentLocation().getLat());
            teacher.setCurrentLng(updateRequest.getCurrentLocation().getLng());
        }
        if (updateRequest.getPreferredLocation() != null) {
            teacher.setPreferredDistrictId(updateRequest.getPreferredLocation().getDistrictId());
            teacher.setPreferredBlockId(updateRequest.getPreferredLocation().getBlockId());
            teacher.setPreferredLat(updateRequest.getPreferredLocation().getLat());
            teacher.setPreferredLng(updateRequest.getPreferredLocation().getLng());
        }
        if (updateRequest.getRadiusKm() != null) {
            teacher.setRadiusKm(updateRequest.getRadiusKm());
        }

        teacher.setUpdatedAt(LocalDateTime.now());
        teacher = teacherRepository.save(teacher);

        updateGeoIndex(teacher);

        return mapToResponse(teacher);
    }

    public void updateLastLogin(Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        teacher.setLastLoginAt(LocalDateTime.now());
        teacherRepository.save(teacher);
    }

    public void updateSubscriptionStatus(Long teacherId, SubscriptionStatus status, String plan, LocalDateTime expiresAt) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        teacher.setSubscriptionStatus(status.getCode());
        teacher.setSubscriptionPlan(plan);
        teacher.setSubscriptionExpiresAt(expiresAt);
        teacherRepository.save(teacher);
    }

    public void updateTeacherStatus(Long teacherId, TeacherStatus status) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        teacher.setStatus(status.getCode());
        teacherRepository.save(teacher);
    }

    private void updateGeoIndex(Teacher teacher) {
        if (teacher.getCurrentLat() == null || teacher.getCurrentLng() == null ||
            teacher.getPreferredLat() == null || teacher.getPreferredLng() == null ||
            teacher.getSubject() == null || teacher.getSchoolType() == null) {
            return;
        }

        java.util.List<TeacherGeoIndex> existingIndexes = teacherGeoIndexRepository.findByTeacherId(teacher.getId());
        TeacherGeoIndex geoIndex;
        if (existingIndexes != null && !existingIndexes.isEmpty()) {
            geoIndex = existingIndexes.get(0);
        } else {
            geoIndex = new TeacherGeoIndex();
            geoIndex.setTeacherId(teacher.getId());
        }

        String geohash = GeohashUtil.encode(teacher.getCurrentLat(), teacher.getCurrentLng());
        geoIndex.setGeohash(geohash);
        geoIndex.setSubject(teacher.getSubject());
        geoIndex.setSchoolType(teacher.getSchoolType());
        geoIndex.setPreferredLat(teacher.getPreferredLat());
        geoIndex.setPreferredLng(teacher.getPreferredLng());
        geoIndex.setLocationType("CURRENT");
        geoIndex.setIsPremium(teacher.getSubscriptionStatus() != null && teacher.getSubscriptionStatus() == SubscriptionStatus.PAID_ACTIVE.getCode());

        teacherGeoIndexRepository.save(geoIndex);
    }

    private TeacherResponse mapToResponse(Teacher teacher) {
        TeacherResponse response = new TeacherResponse();
        response.setId(teacher.getId());
        response.setName(teacher.getName());
        response.setPhone(teacher.getPhone());
        response.setEmail(teacher.getEmail());
        response.setPhoneVerified(teacher.getPhoneVerified());
        response.setEmailVerified(teacher.getEmailVerified());
        response.setGender(teacher.getGender() != null ? Gender.fromCode(teacher.getGender()) : null);
        response.setEmployeeId(teacher.getEmployeeId());
        response.setUdiseCode(teacher.getUdiseCode());
        response.setSchoolName(teacher.getSchoolName());
        response.setSubject(teacher.getSubject() != null ? Subject.fromCode(teacher.getSubject()) : null);
        response.setSchoolType(teacher.getSchoolType() != null ? SchoolType.fromCode(teacher.getSchoolType()) : null);
        response.setRadiusKm(teacher.getRadiusKm());
        response.setStatus(teacher.getStatus() != null ? TeacherStatus.fromCode(teacher.getStatus()) : null);
        response.setSubscriptionStatus(teacher.getSubscriptionStatus() != null ? SubscriptionStatus.fromCode(teacher.getSubscriptionStatus()) : null);
        response.setSubscriptionPlan(teacher.getSubscriptionPlan());
        response.setSubscriptionExpiresAt(teacher.getSubscriptionExpiresAt());
        response.setReferralCode(teacher.getReferralCode());
        response.setReferralCount(teacher.getReferralCount());
        response.setCreatedAt(teacher.getCreatedAt());
        response.setLastLoginAt(teacher.getLastLoginAt());

        // Map current location
        if (teacher.getCurrentDistrictId() != null) {
            TeacherResponse.LocationInfo currentLocation = new TeacherResponse.LocationInfo();
            currentLocation.setDistrictId(teacher.getCurrentDistrictId());
            currentLocation.setBlockId(teacher.getCurrentBlockId());
            currentLocation.setLat(teacher.getCurrentLat());
            currentLocation.setLng(teacher.getCurrentLng());
            response.setCurrentLocation(currentLocation);
        }

        // Map preferred location
        if (teacher.getPreferredDistrictId() != null) {
            TeacherResponse.LocationInfo preferredLocation = new TeacherResponse.LocationInfo();
            preferredLocation.setDistrictId(teacher.getPreferredDistrictId());
            preferredLocation.setBlockId(teacher.getPreferredBlockId());
            preferredLocation.setLat(teacher.getPreferredLat());
            preferredLocation.setLng(teacher.getPreferredLng());
            response.setPreferredLocation(preferredLocation);
        }

        return response;
    }
}