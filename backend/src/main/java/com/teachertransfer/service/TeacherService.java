package com.teachertransfer.service;

import com.teachertransfer.dto.teacher.TeacherResponse;
import com.teachertransfer.entity.Teacher;
import com.teachertransfer.entity.TeacherGeoIndex;
import com.teachertransfer.enums.*;
import com.teachertransfer.entity.Block;
import com.teachertransfer.repository.BlockRepository;
import com.teachertransfer.repository.TeacherGeoIndexRepository;
import com.teachertransfer.repository.TeacherRepository;
import com.teachertransfer.util.GeohashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    @Lazy
    private InterestService interestService;

    public Teacher getCurrentTeacher() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return teacherRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
    }

    public TeacherResponse getTeacherProfile(Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        touchInteraction(teacher);
        return mapToResponse(teacher);
    }

    public TeacherResponse getCurrentTeacherProfile() {
        Teacher teacher = getCurrentTeacher();
        touchInteraction(teacher);
        return mapToResponse(teacher);
    }

    public TeacherResponse updateTeacherProfile(Long teacherId, TeacherResponse updateRequest) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        boolean majorChange = false;
        boolean rebuildCache = false;

        if (updateRequest.getName() != null) teacher.setName(updateRequest.getName());
        if (updateRequest.getEmail() != null) teacher.setEmail(updateRequest.getEmail());
        if (updateRequest.getGender() != null) teacher.setGender(updateRequest.getGender().getCode());
        if (updateRequest.getEmployeeId() != null) teacher.setEmployeeId(updateRequest.getEmployeeId());
        if (updateRequest.getUdiseCode() != null) teacher.setUdiseCode(updateRequest.getUdiseCode());
        if (updateRequest.getSchoolName() != null) teacher.setSchoolName(updateRequest.getSchoolName());
        if (updateRequest.getSubject() != null &&
            !updateRequest.getSubject().equals(teacher.getSubject() != null ? Subject.fromCode(teacher.getSubject()) : null)) {
            teacher.setSubject(updateRequest.getSubject().getCode());
            majorChange = true;
        }
        if (updateRequest.getSchoolType() != null &&
            !updateRequest.getSchoolType().equals(teacher.getSchoolType() != null ? SchoolType.fromCode(teacher.getSchoolType()) : null)) {
            teacher.setSchoolType(updateRequest.getSchoolType().getCode());
            majorChange = true;
        }
        boolean currentLatLngChanged = false;
        if (updateRequest.getCurrentLocation() != null) {
            Integer oldBlockId = teacher.getCurrentBlockId();
            Integer oldDistrictId = teacher.getCurrentDistrictId();
            currentLatLngChanged = teacher.getCurrentLat() != null && (
                Math.abs(updateRequest.getCurrentLocation().getLat() - teacher.getCurrentLat()) > 0.00001 ||
                Math.abs(updateRequest.getCurrentLocation().getLng() - teacher.getCurrentLng()) > 0.00001);
            teacher.setCurrentDistrictId(updateRequest.getCurrentLocation().getDistrictId());
            teacher.setCurrentBlockId(updateRequest.getCurrentLocation().getBlockId());
            teacher.setCurrentLat(updateRequest.getCurrentLocation().getLat());
            teacher.setCurrentLng(updateRequest.getCurrentLocation().getLng());
            if (!java.util.Objects.equals(oldBlockId, teacher.getCurrentBlockId()) ||
                !java.util.Objects.equals(oldDistrictId, teacher.getCurrentDistrictId())) {
                majorChange = true;
            }
        }
        if (teacher.getCurrentLat() == null || teacher.getCurrentLng() == null) {
            if (teacher.getCurrentBlockId() != null) {
                Block currentBlock = blockRepository.findById(teacher.getCurrentBlockId()).orElse(null);
                if (currentBlock != null) {
                    if (teacher.getCurrentLat() == null) teacher.setCurrentLat(currentBlock.getLat());
                    if (teacher.getCurrentLng() == null) teacher.setCurrentLng(currentBlock.getLng());
                }
            }
        }
        boolean preferredLatLngChanged = false;
        if (updateRequest.getPreferredLocation() != null) {
            Integer oldBlockId = teacher.getPreferredBlockId();
            Integer oldDistrictId = teacher.getPreferredDistrictId();
            preferredLatLngChanged = teacher.getPreferredLat() != null && (
                Math.abs(updateRequest.getPreferredLocation().getLat() - teacher.getPreferredLat()) > 0.00001 ||
                Math.abs(updateRequest.getPreferredLocation().getLng() - teacher.getPreferredLng()) > 0.00001);
            teacher.setPreferredDistrictId(updateRequest.getPreferredLocation().getDistrictId());
            teacher.setPreferredBlockId(updateRequest.getPreferredLocation().getBlockId());
            teacher.setPreferredLat(updateRequest.getPreferredLocation().getLat());
            teacher.setPreferredLng(updateRequest.getPreferredLocation().getLng());
            if (!java.util.Objects.equals(oldBlockId, teacher.getPreferredBlockId()) ||
                !java.util.Objects.equals(oldDistrictId, teacher.getPreferredDistrictId())) {
                majorChange = true;
            }
        }
        if (teacher.getPreferredLat() == null || teacher.getPreferredLng() == null) {
            if (teacher.getPreferredBlockId() != null) {
                Block preferredBlock = blockRepository.findById(teacher.getPreferredBlockId()).orElse(null);
                if (preferredBlock != null) {
                    if (teacher.getPreferredLat() == null) teacher.setPreferredLat(preferredBlock.getLat());
                    if (teacher.getPreferredLng() == null) teacher.setPreferredLng(preferredBlock.getLng());
                }
            }
        }
        if (updateRequest.getRadiusKm() != null) {
            teacher.setRadiusKm(updateRequest.getRadiusKm());
        }

        rebuildCache = majorChange || updateRequest.getRadiusKm() != null || currentLatLngChanged || preferredLatLngChanged;

        if (rebuildCache) {
            teacher.setProfileUpdatedAt(LocalDateTime.now());
        }

        teacher.setUpdatedAt(LocalDateTime.now());
        teacher = teacherRepository.save(teacher);

        updateGeoIndex(teacher);

        if (majorChange) {
            interestService.clearAllInteractions(teacher.getId());
        }

        return mapToResponse(teacher);
    }

    public void updateLastLogin(Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        teacher.setLastLoginAt(LocalDateTime.now());
        teacher.setLastInteractionAt(LocalDateTime.now());
        teacherRepository.save(teacher);
    }

    public void touchInteraction(Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId).orElse(null);
        if (teacher != null) {
            touchInteraction(teacher);
        }
    }

    public void touchInteraction(Teacher teacher) {
        teacher.setLastInteractionAt(LocalDateTime.now());
        teacherRepository.save(teacher);
    }

    public void updateSubscriptionStatus(Long teacherId, SubscriptionStatus status, String plan, LocalDateTime expiresAt) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        teacher.setSubscriptionStatus(status.getCode());
        teacher.setSubscriptionPlan(plan);
        teacher.setSubscriptionExpiresAt(expiresAt);
        teacherRepository.save(teacher);
        updateGeoIndex(teacher);
    }

    public void updateTeacherStatus(Long teacherId, TeacherStatus status) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        teacher.setStatus(status.getCode());
        teacherRepository.save(teacher);
    }

    public void updateGeoIndex(Teacher teacher) {
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

        String geohash6 = GeohashUtil.encode(teacher.getPreferredLat(), teacher.getPreferredLng());
        geoIndex.setGeohash6(geohash6);
        String currentGeohash6 = GeohashUtil.encode(teacher.getCurrentLat(), teacher.getCurrentLng());
        geoIndex.setCurrentGeohash6(currentGeohash6);
        geoIndex.setGeohash5(geohash6 != null ? geohash6.substring(0, Math.min(5, geohash6.length())) : null);
        geoIndex.setCurrentGeohash5(currentGeohash6 != null ? currentGeohash6.substring(0, Math.min(5, currentGeohash6.length())) : null);
        geoIndex.setSubject(teacher.getSubject());
        geoIndex.setSchoolType(teacher.getSchoolType());
        geoIndex.setCurrentLat(teacher.getCurrentLat());
        geoIndex.setCurrentLng(teacher.getCurrentLng());
        geoIndex.setPreferredLat(teacher.getPreferredLat());
        geoIndex.setPreferredLng(teacher.getPreferredLng());
        geoIndex.setRadiusKm(teacher.getRadiusKm());
        geoIndex.setIsPremium(teacher.getSubscriptionStatus() != null &&
                teacher.getSubscriptionStatus() == SubscriptionStatus.PAID_ACTIVE.getCode());

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

        if (teacher.getCurrentDistrictId() != null) {
            TeacherResponse.LocationInfo currentLocation = new TeacherResponse.LocationInfo();
            currentLocation.setDistrictId(teacher.getCurrentDistrictId());
            currentLocation.setBlockId(teacher.getCurrentBlockId());
            currentLocation.setLat(teacher.getCurrentLat());
            currentLocation.setLng(teacher.getCurrentLng());
            response.setCurrentLocation(currentLocation);
        }

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
