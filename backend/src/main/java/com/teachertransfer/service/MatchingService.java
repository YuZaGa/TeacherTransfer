package com.teachertransfer.service;

import com.teachertransfer.dto.match.MatchResponse;
import com.teachertransfer.entity.MatchResult;
import com.teachertransfer.entity.Teacher;
import com.teachertransfer.entity.TeacherGeoIndex;
import com.teachertransfer.enums.MatchType;
import com.teachertransfer.enums.SchoolType;
import com.teachertransfer.enums.Subject;
import com.teachertransfer.enums.SubscriptionStatus;
import com.teachertransfer.repository.MatchResultRepository;
import com.teachertransfer.repository.TeacherGeoIndexRepository;
import com.teachertransfer.repository.TeacherRepository;
import com.teachertransfer.repository.TransferInterestRepository;
import com.teachertransfer.util.GeohashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class MatchingService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private MatchResultRepository matchResultRepository;

    @Autowired
    private TeacherGeoIndexRepository teacherGeoIndexRepository;

    @Autowired
    private TransferInterestRepository transferInterestRepository;

    @Value("${app.matching.max-results}")
    private Integer maxResults;

    @Value("${app.ghost.inactive-days}")
    private Integer ghostInactiveDays;

    public List<MatchResponse> getDiscoveryMatches(Long teacherId) {
        Teacher teacher = getTeacherOrThrow(teacherId);
        List<MatchResult> cached = getValidCachedMatches(teacher, MatchType.POTENTIAL);
        if (!cached.isEmpty()) {
            return cached.stream().limit(maxResults).map(m -> mapToResponse(m, teacher, false)).collect(Collectors.toList());
        }
        List<MatchResult> generated = generateAndCacheMatches(teacher);
        return generated.stream().limit(maxResults).map(m -> mapToResponse(m, teacher, false)).collect(Collectors.toList());
    }

    public List<MatchResponse> getMutualMatches(Long teacherId) {
        Teacher teacher = getTeacherOrThrow(teacherId);
        List<MatchResult> cached = getValidCachedMatches(teacher, MatchType.MUTUAL);
        if (!cached.isEmpty()) {
            return cached.stream().map(m -> mapToResponse(m, teacher, true)).collect(Collectors.toList());
        }
        List<MatchResult> all = generateAndCacheMatches(teacher);
        return all.stream()
                .filter(m -> m.getMatchType().equals(MatchType.MUTUAL.getCode()))
                .map(m -> mapToResponse(m, teacher, true))
                .collect(Collectors.toList());
    }

    public List<MatchResponse> getInterestSentMatches(Long teacherId) {
        Teacher teacher = getTeacherOrThrow(teacherId);
        List<MatchResult> cached = getValidCachedMatches(teacher, MatchType.INTEREST_SENT);
        return cached.stream().map(m -> mapToResponse(m, teacher, false)).collect(Collectors.toList());
    }

    public List<MatchResponse> getMatchesForMap(Long teacherId) {
        Teacher teacher = getTeacherOrThrow(teacherId);
        List<MatchResult> all = matchResultRepository.findByTeacherIdOrderByScoreDesc(teacherId);
        return all.stream().map(m -> mapToResponse(m, teacher, m.getMatchType().equals(MatchType.MUTUAL.getCode()))).collect(Collectors.toList());
    }

    public void refreshMatches(Long teacherId) {
        Teacher teacher = getTeacherOrThrow(teacherId);
        matchResultRepository.deleteByTeacherId(teacherId);
        generateAndCacheMatches(teacher);
    }

    private List<MatchResult> generateAndCacheMatches(Teacher teacher) {
        matchResultRepository.deleteByTeacherId(teacher.getId());

        Set<String> searchArea = GeohashUtil.encodeWithNeighbors(
                teacher.getPreferredLat(), teacher.getPreferredLng()
        );

        List<TeacherGeoIndex> candidates = teacherGeoIndexRepository.findCandidatesByGeohashes(
                searchArea, teacher.getSubject(), teacher.getSchoolType(), teacher.getId()
        );

        LocalDateTime ghostCutoff = LocalDateTime.now().minusDays(ghostInactiveDays);
        List<MatchResult> results = new ArrayList<>();

        for (TeacherGeoIndex candidate : candidates) {
            Teacher candidateTeacher = teacherRepository.findById(candidate.getTeacherId()).orElse(null);
            if (candidateTeacher == null) continue;

            if (!isTeacherActive(candidateTeacher)) continue;

            if (candidateTeacher.getLastInteractionAt() != null &&
                    candidateTeacher.getLastInteractionAt().isBefore(ghostCutoff)) continue;

            double distance = haversineDistance(
                    teacher.getPreferredLat(), teacher.getPreferredLng(),
                    candidate.getCurrentLat(), candidate.getCurrentLng()
            );

            if (distance > teacher.getRadiusKm()) continue;

            if (teacher.getPreferredSchoolIds() != null && teacher.getPreferredSchoolIds().length > 0) {
                if (candidateTeacher.getUdiseCode() == null ||
                        !Arrays.asList(teacher.getPreferredSchoolIds()).contains(Long.valueOf(candidateTeacher.getUdiseCode()))) {
                    continue;
                }
            }

            boolean isMutual = isMutualMatch(teacher, candidate, candidateTeacher);

            int matchTypeCode = isMutual ? MatchType.MUTUAL.getCode() : MatchType.POTENTIAL.getCode();
            boolean hasInterestFromMe = transferInterestRepository
                    .findExistingInterest(teacher.getId(), candidate.getTeacherId()).isPresent();
            if (hasInterestFromMe && !isMutual) {
                matchTypeCode = MatchType.INTEREST_SENT.getCode();
            }

            double score = calculateScore(distance, isMutual, candidateTeacher);

            MatchResult result = new MatchResult();
            result.setTeacherId(teacher.getId());
            result.setMatchedTeacherId(candidate.getTeacherId());
            result.setMatchType(matchTypeCode);
            result.setDistanceKm(distance);
            result.setScore(score);
            result.setMatchReason(buildMatchReason(candidateTeacher, distance));
            result.setCreatedAt(LocalDateTime.now());
            result.setMatchGeneratedAt(LocalDateTime.now());
            results.add(result);
        }

        results.sort((a, b) -> {
            int scoreCompare = Double.compare(b.getScore(), a.getScore());
            if (scoreCompare != 0) return scoreCompare;
            return Double.compare(a.getDistanceKm(), b.getDistanceKm());
        });

        List<MatchResult> limited = results.stream().limit(maxResults).collect(Collectors.toList());
        matchResultRepository.saveAll(limited);
        return limited;
    }

    private boolean isMutualMatch(Teacher teacher, TeacherGeoIndex candidate, Teacher candidateTeacher) {
        double reverseDistance = haversineDistance(
                candidate.getPreferredLat(), candidate.getPreferredLng(),
                teacher.getCurrentLat(), teacher.getCurrentLng()
        );
        Integer candidateRadius = candidate.getRadiusKm() != null ? candidate.getRadiusKm() : 30;
        return reverseDistance <= candidateRadius;
    }

    private double calculateScore(double distanceKm, boolean isMutual, Teacher candidateTeacher) {
        double score = 100.0 - (distanceKm * 2.0);
        if (isMutual) score += 20.0;
        if (candidateTeacher.isPaidActive()) score += 10.0;
        return Math.max(0, score);
    }

    private boolean isTeacherActive(Teacher teacher) {
        return teacher.getStatus() != null && teacher.getStatus() == 1;
    }

    private double haversineDistance(double lat1, double lng1, double lat2, double lng2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private String buildMatchReason(Teacher candidate, double distance) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%.1f km away", distance));
        if (candidate.getSubject() != null) {
            sb.append(" • ").append(Subject.fromCode(candidate.getSubject()).getDisplayName());
        }
        if (candidate.getSchoolType() != null) {
            sb.append(" • ").append(SchoolType.fromCode(candidate.getSchoolType()).getDisplayName());
        }
        return sb.toString();
    }

    private List<MatchResult> getValidCachedMatches(Teacher teacher, MatchType matchType) {
        List<MatchResult> all = matchResultRepository.findByTeacherIdAndMatchType(
                teacher.getId(), matchType.getCode()
        );
        LocalDateTime profileUpdatedAt = teacher.getProfileUpdatedAt();
        if (profileUpdatedAt == null) return all;
        return all.stream()
                .filter(m -> m.getMatchGeneratedAt() != null && m.getMatchGeneratedAt().isAfter(profileUpdatedAt))
                .collect(Collectors.toList());
    }

    private Teacher getTeacherOrThrow(Long teacherId) {
        return teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
    }

    private MatchResponse mapToResponse(MatchResult matchResult, Teacher viewer, boolean revealIdentity) {
        MatchResponse response = new MatchResponse();
        response.setId(matchResult.getId());
        response.setTeacherId(matchResult.getTeacherId());
        response.setMatchType(MatchType.fromCode(matchResult.getMatchType()));
        response.setScore(matchResult.getScore());
        response.setDistanceKm(matchResult.getDistanceKm());
        response.setMatchReason(matchResult.getMatchReason());
        response.setIsMutual(matchResult.getMatchType().equals(MatchType.MUTUAL.getCode()));
        response.setCreatedAt(matchResult.getCreatedAt());

        Teacher matchedTeacher = teacherRepository.findById(matchResult.getMatchedTeacherId()).orElse(null);
        if (matchedTeacher == null) return response;

        MatchResponse.TeacherInfo info = new MatchResponse.TeacherInfo();
        info.setId(matchedTeacher.getId());
        info.setDistanceKm(matchResult.getDistanceKm());
        info.setSubject(Subject.fromCode(matchedTeacher.getSubject()).getDisplayName());
        info.setSchoolType(SchoolType.fromCode(matchedTeacher.getSchoolType()).getDisplayName());
        info.setApproxArea("Near " + resolveBlockName(matchedTeacher));

        boolean isMutual = matchResult.getMatchType().equals(MatchType.MUTUAL.getCode());
        info.setIdentityRevealed(isMutual);

        if (isMutual) {
            info.setName(matchedTeacher.getName());
            info.setSchoolName(matchedTeacher.getSchoolName());
            info.setPhone(matchedTeacher.getPhone());
        }

        response.setTeacher(info);
        return response;
    }

    private String resolveBlockName(Teacher teacher) {
        return "Block " + teacher.getCurrentBlockId();
    }
}
